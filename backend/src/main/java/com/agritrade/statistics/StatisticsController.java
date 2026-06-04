package com.agritrade.statistics;

import com.agritrade.auth.AuthService;
import com.agritrade.common.Result;
import com.agritrade.merchant.Merchant;
import com.agritrade.merchant.MerchantService;
import com.agritrade.order.TradeOrder;
import com.agritrade.order.TradeOrderMapper;
import com.agritrade.product.Product;
import com.agritrade.product.ProductMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final AuthService authService;
    private final MerchantService merchantService;
    private final TradeOrderMapper orderMapper;
    private final ProductMapper productMapper;

    @GetMapping("/api/admin/statistics/overview")
    public Result<Map<String, Object>> adminOverview() {
        authService.requireRole("ADMIN");
        List<TradeOrder> orders = orderMapper.selectList(null);
        BigDecimal sales = orders.stream()
                .filter(item -> "COMPLETED".equals(item.getOrderStatus()) || "WAIT_SHIPMENT".equals(item.getOrderStatus()) || "SHIPPED".equals(item.getOrderStatus()))
                .map(TradeOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.ok(Map.of(
                "orderCount", orders.size(),
                "salesAmount", sales,
                "productCount", productMapper.selectCount(null)
        ));
    }

    @GetMapping("/api/admin/statistics/products/top-sales")
    public Result<List<Product>> topProducts() {
        authService.requireRole("ADMIN");
        return Result.ok(productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .orderByDesc(Product::getSalesCount)
                .last("limit 10")));
    }

    @GetMapping("/api/admin/statistics/orders/trend")
    public Result<List<TradeOrder>> orderTrend() {
        authService.requireRole("ADMIN");
        return Result.ok(orderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery()
                .orderByDesc(TradeOrder::getCreateTime)
                .last("limit 30")));
    }

    @GetMapping("/api/admin/statistics/categories/sales-ratio")
    public Result<List<Product>> categorySalesRatio() {
        authService.requireRole("ADMIN");
        return Result.ok(productMapper.selectList(Wrappers.<Product>lambdaQuery().orderByDesc(Product::getSalesCount)));
    }

    @GetMapping("/api/admin/statistics/merchants/top-sales")
    public Result<List<TradeOrder>> topMerchants() {
        authService.requireRole("ADMIN");
        return Result.ok(orderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery().orderByDesc(TradeOrder::getPayAmount).last("limit 10")));
    }

    @GetMapping("/api/merchant/statistics/overview")
    public Result<Map<String, Object>> merchantOverview() {
        Merchant merchant = merchantService.currentMerchant();
        List<TradeOrder> orders = orderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery().eq(TradeOrder::getMerchantId, merchant.getId()));
        BigDecimal sales = orders.stream().map(TradeOrder::getPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.ok(Map.of("orderCount", orders.size(), "salesAmount", sales));
    }

    @GetMapping("/api/merchant/statistics/orders/trend")
    public Result<List<TradeOrder>> merchantTrend() {
        Merchant merchant = merchantService.currentMerchant();
        return Result.ok(orderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery()
                .eq(TradeOrder::getMerchantId, merchant.getId())
                .orderByDesc(TradeOrder::getCreateTime)
                .last("limit 30")));
    }
}
