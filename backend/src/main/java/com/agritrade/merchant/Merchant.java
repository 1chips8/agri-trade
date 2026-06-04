package com.agritrade.merchant;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {
    private Long userId;
    private String merchantName;
    private String contactName;
    private String contactPhone;
    private String originAddress;
    private String description;
    private String licenseImage;
    private String status;
    private BigDecimal totalSalesAmount;
    private Integer totalOrderCount;
}
