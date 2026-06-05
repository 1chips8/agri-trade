package com.agritrade.merchant;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_apply")
public class MerchantApply extends BaseEntity {
    private Long userId;
    @NotBlank
    private String merchantName;
    @NotBlank
    private String contactName;
    @NotBlank
    private String contactPhone;
    @NotBlank
    private String originAddress;
    private String description;
    private String licenseImage;
    private String applyStatus;
    private Long reviewerId;
    private LocalDateTime reviewTime;
    private String rejectReason;
}
