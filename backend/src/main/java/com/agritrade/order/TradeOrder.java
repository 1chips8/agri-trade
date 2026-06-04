package com.agritrade.order;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("trade_order")
public class TradeOrder extends BaseEntity {
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String orderStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private String remark;
}
