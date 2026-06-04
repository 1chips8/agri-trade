package com.agritrade.category;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_category")
public class ProductCategory extends BaseEntity {
    private String categoryName;
    private Long parentId;
    private Integer sortOrder;
    private String status;
}
