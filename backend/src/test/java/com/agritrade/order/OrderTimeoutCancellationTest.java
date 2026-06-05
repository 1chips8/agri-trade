package com.agritrade.order;

import com.agritrade.payment.PaymentRecord;
import com.agritrade.payment.PaymentRecordMapper;
import com.agritrade.product.Product;
import com.agritrade.product.ProductMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local-h2")
@Transactional
class OrderTimeoutCancellationTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private TradeOrderMapper orderMapper;
    @Autowired
    private TradeOrderItemMapper orderItemMapper;
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    @Test
    void cancelExpiredPendingOrdersReturnsStockAndClosesPayment() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 12, 0);
        Product product = insertProduct(6);
        TradeOrder expired = insertOrder("PENDING_PAYMENT", now.minusMinutes(31));
        insertOrderItem(expired, product, 4);
        insertPayment(expired, "PENDING");

        int cancelled = orderService.cancelExpiredPendingOrders(now, Duration.ofMinutes(30));

        TradeOrder updatedOrder = orderMapper.selectById(expired.getId());
        PaymentRecord updatedPayment = paymentByOrder(expired.getId());
        Product updatedProduct = productMapper.selectById(product.getId());
        List<InventoryLog> logs = inventoryLogMapper.selectList(Wrappers.<InventoryLog>lambdaQuery()
                .eq(InventoryLog::getOrderId, expired.getId()));
        assertThat(cancelled).isEqualTo(1);
        assertThat(updatedOrder.getOrderStatus()).isEqualTo("CANCELLED");
        assertThat(updatedOrder.getCancelReason()).isEqualTo("支付超时自动取消");
        assertThat(updatedOrder.getCancelTime()).isEqualTo(now);
        assertThat(updatedPayment.getPayStatus()).isEqualTo("CLOSED");
        assertThat(updatedProduct.getStock()).isEqualTo(10);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getChangeType()).isEqualTo("RETURN");
    }

    @Test
    void cancelExpiredPendingOrdersSkipsRecentAndPaidOrders() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 5, 12, 0);
        Product recentProduct = insertProduct(6);
        Product paidProduct = insertProduct(6);
        TradeOrder recent = insertOrder("PENDING_PAYMENT", now.minusMinutes(29));
        TradeOrder paid = insertOrder("WAIT_SHIPMENT", now.minusMinutes(60));
        insertOrderItem(recent, recentProduct, 4);
        insertOrderItem(paid, paidProduct, 4);
        insertPayment(recent, "PENDING");
        insertPayment(paid, "SUCCESS");

        int cancelled = orderService.cancelExpiredPendingOrders(now, Duration.ofMinutes(30));

        assertThat(cancelled).isZero();
        assertThat(orderMapper.selectById(recent.getId()).getOrderStatus()).isEqualTo("PENDING_PAYMENT");
        assertThat(orderMapper.selectById(paid.getId()).getOrderStatus()).isEqualTo("WAIT_SHIPMENT");
        assertThat(productMapper.selectById(recentProduct.getId()).getStock()).isEqualTo(6);
        assertThat(productMapper.selectById(paidProduct.getId()).getStock()).isEqualTo(6);
    }

    private Product insertProduct(int stock) {
        Product product = new Product();
        product.setMerchantId(1L);
        product.setCategoryId(1L);
        product.setProductName("超时取消测试商品");
        product.setPrice(new BigDecimal("12.50"));
        product.setStock(stock);
        product.setAuditStatus("APPROVED");
        product.setSaleStatus("ON_SALE");
        productMapper.insert(product);
        return product;
    }

    private TradeOrder insertOrder(String status, LocalDateTime createTime) {
        TradeOrder order = new TradeOrder();
        order.setOrderNo("TIMEOUT-" + System.nanoTime());
        order.setUserId(11L);
        order.setMerchantId(1L);
        order.setTotalAmount(new BigDecimal("50.00"));
        order.setPayAmount(new BigDecimal("50.00"));
        order.setOrderStatus(status);
        order.setCreateTime(createTime);
        order.setUpdateTime(createTime);
        orderMapper.insert(order);
        return order;
    }

    private void insertOrderItem(TradeOrder order, Product product, int quantity) {
        TradeOrderItem item = new TradeOrderItem();
        item.setOrderId(order.getId());
        item.setOrderNo(order.getOrderNo());
        item.setProductId(product.getId());
        item.setMerchantId(product.getMerchantId());
        item.setProductName(product.getProductName());
        item.setProductPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        item.setCreateTime(order.getCreateTime());
        orderItemMapper.insert(item);
    }

    private void insertPayment(TradeOrder order, String status) {
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PAY-" + order.getOrderNo());
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setUserId(order.getUserId());
        record.setPayAmount(order.getPayAmount());
        record.setPayType("MOCK");
        record.setPayStatus(status);
        record.setCreateTime(order.getCreateTime());
        record.setUpdateTime(order.getCreateTime());
        paymentRecordMapper.insert(record);
    }

    private PaymentRecord paymentByOrder(Long orderId) {
        return paymentRecordMapper.selectOne(Wrappers.<PaymentRecord>lambdaQuery()
                .eq(PaymentRecord::getOrderId, orderId));
    }
}
