package com.agritrade.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local-h2")
@Transactional
class TradeOrderMapperStatusTest {
    @Autowired
    private TradeOrderMapper orderMapper;

    @Test
    void markPaidOnlyTransitionsPendingOrderOnce() {
        TradeOrder order = insertOrder("PENDING_PAYMENT");
        LocalDateTime payTime = LocalDateTime.of(2026, 6, 5, 10, 0);

        int first = orderMapper.markPaidIfPending(order.getId(), order.getUserId(), payTime);
        int second = orderMapper.markPaidIfPending(order.getId(), order.getUserId(), payTime.plusMinutes(1));

        TradeOrder updated = orderMapper.selectById(order.getId());
        assertThat(first).isEqualTo(1);
        assertThat(second).isZero();
        assertThat(updated.getOrderStatus()).isEqualTo("WAIT_SHIPMENT");
        assertThat(updated.getPayTime()).isEqualTo(payTime);
    }

    @Test
    void cancelOnlyTransitionsPendingOrderOnce() {
        TradeOrder order = insertOrder("PENDING_PAYMENT");
        LocalDateTime cancelTime = LocalDateTime.of(2026, 6, 5, 10, 0);

        int first = orderMapper.cancelIfPending(order.getId(), order.getUserId(), "用户取消", cancelTime);
        int second = orderMapper.cancelIfPending(order.getId(), order.getUserId(), "重复取消", cancelTime.plusMinutes(1));

        TradeOrder updated = orderMapper.selectById(order.getId());
        assertThat(first).isEqualTo(1);
        assertThat(second).isZero();
        assertThat(updated.getOrderStatus()).isEqualTo("CANCELLED");
        assertThat(updated.getCancelReason()).isEqualTo("用户取消");
        assertThat(updated.getCancelTime()).isEqualTo(cancelTime);
    }

    @Test
    void shipOnlyTransitionsMerchantWaitingOrderOnce() {
        TradeOrder order = insertOrder("WAIT_SHIPMENT");
        LocalDateTime shipTime = LocalDateTime.of(2026, 6, 5, 10, 0);

        int first = orderMapper.shipIfWaiting(order.getId(), order.getMerchantId(), shipTime);
        int second = orderMapper.shipIfWaiting(order.getId(), order.getMerchantId(), shipTime.plusMinutes(1));

        TradeOrder updated = orderMapper.selectById(order.getId());
        assertThat(first).isEqualTo(1);
        assertThat(second).isZero();
        assertThat(updated.getOrderStatus()).isEqualTo("SHIPPED");
        assertThat(updated.getShipTime()).isEqualTo(shipTime);
    }

    @Test
    void completeOnlyTransitionsUsersShippedOrderOnce() {
        TradeOrder order = insertOrder("SHIPPED");
        LocalDateTime completeTime = LocalDateTime.of(2026, 6, 5, 10, 0);

        int first = orderMapper.completeIfShipped(order.getId(), order.getUserId(), completeTime);
        int second = orderMapper.completeIfShipped(order.getId(), order.getUserId(), completeTime.plusMinutes(1));

        TradeOrder updated = orderMapper.selectById(order.getId());
        assertThat(first).isEqualTo(1);
        assertThat(second).isZero();
        assertThat(updated.getOrderStatus()).isEqualTo("COMPLETED");
        assertThat(updated.getCompleteTime()).isEqualTo(completeTime);
    }

    private TradeOrder insertOrder(String status) {
        TradeOrder order = new TradeOrder();
        order.setOrderNo("STATUS-" + status + "-" + System.nanoTime());
        order.setUserId(11L);
        order.setMerchantId(22L);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setPayAmount(new BigDecimal("100.00"));
        order.setOrderStatus(status);
        orderMapper.insert(order);
        return order;
    }
}
