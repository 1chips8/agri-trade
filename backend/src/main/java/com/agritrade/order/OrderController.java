package com.agritrade.order;

import com.agritrade.common.BizException;
import com.agritrade.common.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/orders")
    public Result<List<TradeOrder>> create(@Valid @RequestBody CreateOrderRequest request) {
        return Result.ok(orderService.create(request));
    }

    @GetMapping("/api/orders")
    public Result<List<TradeOrder>> myOrders() {
        return Result.ok(orderService.myOrders());
    }

    @GetMapping("/api/orders/{orderId}")
    public Result<OrderDetailResponse> detail(@PathVariable Long orderId) {
        validateOrderId(orderId);
        return Result.ok(orderService.detail(orderId));
    }

    @PostMapping("/api/orders/{orderId}/cancel")
    public Result<Void> cancel(@PathVariable Long orderId, @RequestBody(required = false) CancelRequest request) {
        validateOrderId(orderId);
        orderService.cancel(orderId, request == null ? null : request.getReason());
        return Result.ok();
    }

    @GetMapping("/api/merchant/orders")
    public Result<List<TradeOrder>> merchantOrders() {
        return Result.ok(orderService.merchantOrders());
    }

    @PostMapping("/api/merchant/orders/{orderId}/ship")
    public Result<Void> ship(@PathVariable Long orderId) {
        validateOrderId(orderId);
        orderService.ship(orderId);
        return Result.ok();
    }

    @PostMapping("/api/orders/{orderId}/receive")
    public Result<Void> receive(@PathVariable Long orderId) {
        validateOrderId(orderId);
        orderService.receive(orderId);
        return Result.ok();
    }

    @GetMapping("/api/admin/orders")
    public Result<List<TradeOrder>> adminOrders() {
        return Result.ok(orderService.adminOrders());
    }

    @Data
    static class CancelRequest {
        private String reason;
    }

    private void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BizException("订单ID必须为正数");
        }
    }
}
