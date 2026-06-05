package com.agritrade.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "agri.order.timeout", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OrderTimeoutScheduler {
    private final OrderService orderService;

    @Value("${agri.order.timeout.minutes:30}")
    private long timeoutMinutes;

    @Scheduled(fixedDelayString = "${agri.order.timeout.scan-delay-ms:60000}")
    public void cancelExpiredPendingOrders() {
        int cancelled = orderService.cancelExpiredPendingOrders(LocalDateTime.now(), Duration.ofMinutes(timeoutMinutes));
        if (cancelled > 0) {
            log.info("Cancelled {} expired pending orders", cancelled);
        }
    }
}
