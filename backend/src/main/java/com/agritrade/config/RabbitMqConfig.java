package com.agritrade.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String NOTICE_EXCHANGE = "notice.exchange";
    public static final String ORDER_DELAY_CANCEL_QUEUE = "order.delay.cancel.queue";
    public static final String NOTICE_CREATE_QUEUE = "notice.create.queue";
    public static final String ORDER_CANCEL_DELAY_KEY = "order.cancel.delay";
    public static final String NOTICE_CREATE_KEY = "notice.create";

    @Bean
    public DirectExchange orderExchange() {
        return ExchangeBuilder.directExchange(ORDER_EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange noticeExchange() {
        return ExchangeBuilder.directExchange(NOTICE_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue orderDelayCancelQueue() {
        return QueueBuilder.durable(ORDER_DELAY_CANCEL_QUEUE).build();
    }

    @Bean
    public Queue noticeCreateQueue() {
        return QueueBuilder.durable(NOTICE_CREATE_QUEUE).build();
    }

    @Bean
    public Binding orderDelayCancelBinding() {
        return BindingBuilder.bind(orderDelayCancelQueue()).to(orderExchange()).with(ORDER_CANCEL_DELAY_KEY);
    }

    @Bean
    public Binding noticeCreateBinding() {
        return BindingBuilder.bind(noticeCreateQueue()).to(noticeExchange()).with(NOTICE_CREATE_KEY);
    }
}
