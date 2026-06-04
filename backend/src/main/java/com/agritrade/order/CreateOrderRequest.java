package com.agritrade.order;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
}
