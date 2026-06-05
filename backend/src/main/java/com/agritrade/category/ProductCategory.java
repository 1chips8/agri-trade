package com.agritrade.category;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_category")
public class ProductCategory extends BaseEntity {
    @NotBlank
    private String categoryName;
    private Long parentId;
    @Min(0)
    private Integer sortOrder;
    @Pattern(regexp = "ENABLED|DISABLED")
    private String status;
}
