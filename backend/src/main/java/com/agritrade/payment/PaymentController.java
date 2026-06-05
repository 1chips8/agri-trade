package com.agritrade.payment;

import com.agritrade.common.BizException;
import com.agritrade.common.Result;
import com.agritrade.order.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final OrderService orderService;

    @PostMapping("/mock-pay")
    public Result<Void> mockPay(@Valid @RequestBody MockPayRequest request) {
        orderService.markPaid(request.getOrderId());
        return Result.ok();
    }

    @GetMapping("/order/{orderId}")
    public Result<PaymentRecord> byOrder(@PathVariable Long orderId) {
        validateOrderId(orderId);
        return Result.ok(orderService.paymentByOrder(orderId));
    }

    private void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BizException("订单ID必须为正数");
        }
    }

    @Data
    static class MockPayRequest {
        @NotNull
        @Min(1)
        private Long orderId;
    }
}
