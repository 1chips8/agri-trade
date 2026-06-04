package com.agritrade.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory_log")
public class InventoryLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long merchantId;
    private Long orderId;
    private String orderNo;
    private String changeType;
    private Integer changeQuantity;
    private Integer beforeStock;
    private Integer afterStock;
    private String remark;
    private LocalDateTime createTime;
}
