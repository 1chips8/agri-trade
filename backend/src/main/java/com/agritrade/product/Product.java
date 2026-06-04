package com.agritrade.product;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {
    private Long merchantId;
    private Long categoryId;
    private String productName;
    private String productImage;
    private String description;
    private String originPlace;
    private BigDecimal price;
    private Integer stock;
    private String unit;
    private String shelfLife;
    private String auditStatus;
    private String saleStatus;
    private Integer salesCount;
    private Integer reviewCount;
    private BigDecimal averageRating;
}
