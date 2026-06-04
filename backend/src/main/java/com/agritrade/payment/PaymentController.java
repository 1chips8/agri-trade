package com.agritrade.payment;

import com.agritrade.common.Result;
import com.agritrade.order.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final OrderService orderService;

    @PostMapping("/mock-pay")
    public Result<Void> mockPay(@RequestBody MockPayRequest request) {
        orderService.markPaid(request.getOrderId());
        return Result.ok();
    }

    @GetMapping("/order/{orderId}")
    public Result<PaymentRecord> byOrder(@PathVariable Long orderId) {
        return Result.ok(orderService.paymentByOrder(orderId));
    }

    @Data
    static class MockPayRequest {
        private Long orderId;
    }
}
