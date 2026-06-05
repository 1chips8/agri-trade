package com.agritrade.order;

import cn.dev33.satoken.stp.StpUtil;
import com.agritrade.auth.AuthService;
import com.agritrade.cart.CartItem;
import com.agritrade.cart.CartItemMapper;
import com.agritrade.common.BizException;
import com.agritrade.merchant.Merchant;
import com.agritrade.merchant.MerchantService;
import com.agritrade.message.MessageService;
import com.agritrade.payment.PaymentRecord;
import com.agritrade.payment.PaymentRecordMapper;
import com.agritrade.product.Product;
import com.agritrade.product.ProductMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final TradeOrderMapper orderMapper;
    private final TradeOrderItemMapper orderItemMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final MerchantService merchantService;
    private final AuthService authService;
    private final MessageService messageService;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public List<TradeOrder> create(CreateOrderRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        String lockKey = "order:submit:lock:" + userId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(5));
        if (Boolean.FALSE.equals(locked)) {
            throw new BizException("订单重复提交");
        }
        try {
            List<CartItem> selected = cartItemMapper.selectList(Wrappers.<CartItem>lambdaQuery()
                    .eq(CartItem::getUserId, userId)
                    .eq(CartItem::getSelected, 1));
            if (selected.isEmpty()) {
                throw new BizException("请选择购物车商品");
            }
            Map<Long, List<CartItem>> byMerchant = new LinkedHashMap<>();
            for (CartItem item : selected) {
                byMerchant.computeIfAbsent(item.getMerchantId(), k -> new ArrayList<>()).add(item);
            }
            List<TradeOrder> orders = new ArrayList<>();
            for (Map.Entry<Long, List<CartItem>> entry : byMerchant.entrySet()) {
                TradeOrder order = buildOrder(userId, entry.getKey(), request);
                orderMapper.insert(order);
                BigDecimal total = BigDecimal.ZERO;
                for (CartItem cartItem : entry.getValue()) {
                    Product product = productMapper.selectById(cartItem.getProductId());
                    if (product == null || !"ON_SALE".equals(product.getSaleStatus())) {
                        throw new BizException("商品不可购买");
                    }
                    if (productMapper.decrementStockIfAvailable(product.getId(), cartItem.getQuantity()) == 0) {
                        throw new BizException(product.getProductName() + "库存不足");
                    }
                    product = productMapper.selectById(product.getId());
                    int before = product.getStock() + cartItem.getQuantity();
                    total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    createOrderItem(order, product, cartItem.getQuantity());
                    createInventoryLog(order, product, cartItem.getQuantity(), before, product.getStock(), "SALE_LOCK");
                }
                order.setTotalAmount(total);
                order.setPayAmount(total);
                orderMapper.updateById(order);
                createPayment(order, userId);
                orders.add(order);
            }
            for (CartItem item : selected) {
                cartItemMapper.deleteById(item.getId());
            }
            return orders;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    public List<TradeOrder> myOrders() {
        return orderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery()
                .eq(TradeOrder::getUserId, StpUtil.getLoginIdAsLong())
                .orderByDesc(TradeOrder::getCreateTime));
    }

    public OrderDetailResponse detail(Long orderId) {
        TradeOrder order = orderMapper.selectById(orderId);
        Long userId = StpUtil.getLoginIdAsLong();
        if (order == null || (!order.getUserId().equals(userId) && !canMerchantAccess(order))) {
            throw new BizException("订单不存在");
        }
        return new OrderDetailResponse(order, items(orderId));
    }

    @Transactional
    public void cancel(Long orderId, String reason) {
        TradeOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new BizException("订单不存在");
        }
        if (!cancelPending(order, reason == null ? "用户取消" : reason, LocalDateTime.now())) {
            throw new BizException("订单状态不可取消");
        }
    }

    @Transactional
    public int cancelExpiredPendingOrders(LocalDateTime now, Duration timeout) {
        LocalDateTime cutoff = now.minus(timeout);
        List<TradeOrder> expiredOrders = orderMapper.selectExpiredPendingOrders(cutoff, 100);
        int cancelled = 0;
        for (TradeOrder order : expiredOrders) {
            if (cancelPending(order, "支付超时自动取消", now)) {
                cancelled++;
            }
        }
        return cancelled;
    }

    public List<TradeOrder> merchantOrders() {
        Merchant merchant = merchantService.currentMerchant();
        return orderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery()
                .eq(TradeOrder::getMerchantId, merchant.getId())
                .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> adminOrders() {
        authService.requireRole("ADMIN");
        return orderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery().orderByDesc(TradeOrder::getCreateTime));
    }

    @Transactional
    public void ship(Long orderId) {
        TradeOrder order = merchantOrder(orderId);
        LocalDateTime shipTime = LocalDateTime.now();
        if (orderMapper.shipIfWaiting(orderId, order.getMerchantId(), shipTime) == 0) {
            throw new BizException("订单状态不可发货");
        }
        messageService.create(order.getUserId(), "订单已发货", "订单 " + order.getOrderNo() + " 已发货", "ORDER", order.getId(), "ORDER");
    }

    @Transactional
    public void receive(Long orderId) {
        TradeOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new BizException("订单不存在");
        }
        if (orderMapper.completeIfShipped(orderId, order.getUserId(), LocalDateTime.now()) == 0) {
            throw new BizException("订单状态不可收货");
        }
    }

    @Transactional
    public void markPaid(Long orderId) {
        TradeOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new BizException("订单不存在");
        }
        LocalDateTime payTime = LocalDateTime.now();
        if (orderMapper.markPaidIfPending(orderId, order.getUserId(), payTime) == 0) {
            throw new BizException("订单状态不可支付");
        }
        PaymentRecord record = paymentRecordMapper.selectOne(Wrappers.<PaymentRecord>lambdaQuery().eq(PaymentRecord::getOrderId, orderId));
        record.setPayStatus("SUCCESS");
        record.setPayType("MOCK");
        record.setPayTime(payTime);
        record.setUpdateTime(payTime);
        paymentRecordMapper.updateById(record);
        messageService.create(order.getUserId(), "支付成功", "订单 " + order.getOrderNo() + " 支付成功", "PAYMENT", order.getId(), "ORDER");
    }

    public PaymentRecord paymentByOrder(Long orderId) {
        return paymentRecordMapper.selectOne(Wrappers.<PaymentRecord>lambdaQuery().eq(PaymentRecord::getOrderId, orderId));
    }

    private TradeOrder buildOrder(Long userId, Long merchantId, CreateOrderRequest request) {
        TradeOrder order = new TradeOrder();
        order.setOrderNo("AT" + System.currentTimeMillis() + (int) (Math.random() * 1000));
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        order.setOrderStatus("PENDING_PAYMENT");
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setRemark(request.getRemark());
        return order;
    }

    private void createOrderItem(TradeOrder order, Product product, Integer quantity) {
        TradeOrderItem item = new TradeOrderItem();
        item.setOrderId(order.getId());
        item.setOrderNo(order.getOrderNo());
        item.setProductId(product.getId());
        item.setMerchantId(product.getMerchantId());
        item.setProductName(product.getProductName());
        item.setProductImage(product.getProductImage());
        item.setProductPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        item.setCreateTime(LocalDateTime.now());
        orderItemMapper.insert(item);
    }

    private void createPayment(TradeOrder order, Long userId) {
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PAY" + order.getOrderNo());
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setUserId(userId);
        record.setPayAmount(order.getPayAmount());
        record.setPayType("MOCK");
        record.setPayStatus("PENDING");
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        paymentRecordMapper.insert(record);
    }

    private void createInventoryLog(TradeOrder order, Product product, Integer quantity, int before, int after, String type) {
        InventoryLog log = new InventoryLog();
        log.setProductId(product.getId());
        log.setMerchantId(product.getMerchantId());
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        log.setChangeType(type);
        log.setChangeQuantity(quantity);
        log.setBeforeStock(before);
        log.setAfterStock(after);
        log.setRemark("订单库存变更");
        log.setCreateTime(LocalDateTime.now());
        inventoryLogMapper.insert(log);
    }

    private List<TradeOrderItem> items(Long orderId) {
        return orderItemMapper.selectList(Wrappers.<TradeOrderItem>lambdaQuery().eq(TradeOrderItem::getOrderId, orderId));
    }

    private TradeOrder merchantOrder(Long orderId) {
        Merchant merchant = merchantService.currentMerchant();
        TradeOrder order = orderMapper.selectById(orderId);
        if (order == null || !merchant.getId().equals(order.getMerchantId())) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private boolean canMerchantAccess(TradeOrder order) {
        try {
            return merchantService.currentMerchant().getId().equals(order.getMerchantId());
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean cancelPending(TradeOrder order, String reason, LocalDateTime cancelTime) {
        if (orderMapper.cancelIfPending(order.getId(), order.getUserId(), reason, cancelTime) == 0) {
            return false;
        }
        for (TradeOrderItem item : items(order.getId())) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || productMapper.incrementStock(product.getId(), item.getQuantity()) == 0) {
                throw new BizException("商品不存在，无法返还库存");
            }
            product = productMapper.selectById(product.getId());
            int before = product.getStock() - item.getQuantity();
            createInventoryLog(order, product, item.getQuantity(), before, product.getStock(), "RETURN");
        }
        PaymentRecord record = paymentByOrder(order.getId());
        if (record != null) {
            record.setPayStatus("CLOSED");
            record.setUpdateTime(cancelTime);
            paymentRecordMapper.updateById(record);
        }
        return true;
    }
}
