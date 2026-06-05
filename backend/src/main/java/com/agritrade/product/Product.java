package com.agritrade.product;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {
    private Long merchantId;
    @NotNull
    private Long categoryId;
    @NotBlank
    private String productName;
    private String productImage;
    private String description;
    private String originPlace;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
    @NotNull
    @Min(0)
    private Integer stock;
    @NotBlank
    private String unit;
    private String shelfLife;
    private String auditStatus;
    private String saleStatus;
    private Integer salesCount;
    private Integer reviewCount;
    private BigDecimal averageRating;
}
