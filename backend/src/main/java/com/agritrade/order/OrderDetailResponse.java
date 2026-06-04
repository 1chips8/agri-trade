package com.agritrade.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailResponse {
    private TradeOrder order;
    private List<TradeOrderItem> items;
}
