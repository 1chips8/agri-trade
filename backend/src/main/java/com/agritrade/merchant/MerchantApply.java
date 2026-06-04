package com.agritrade.merchant;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_apply")
public class MerchantApply extends BaseEntity {
    private Long userId;
    private String merchantName;
    private String contactName;
    private String contactPhone;
    private String originAddress;
    private String description;
    private String licenseImage;
    private String applyStatus;
    private Long reviewerId;
    private LocalDateTime reviewTime;
    private String rejectReason;
}
