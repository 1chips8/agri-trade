package com.agritrade.cart;

import com.agritrade.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cart_item")
public class CartItem extends BaseEntity {
    private Long userId;
    private Long productId;
    private Long merchantId;
    private Integer quantity;
    private Integer selected;
}
