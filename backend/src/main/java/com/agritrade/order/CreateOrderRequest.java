package com.agritrade.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderRequest {
    @NotBlank
    private String receiverName;
    @NotBlank
    private String receiverPhone;
    @NotBlank
    private String receiverAddress;
    private String remark;
}
