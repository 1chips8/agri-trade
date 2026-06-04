package com.agritrade.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {
    @Update("UPDATE product " +
            "SET stock = stock - #{quantity}, update_time = NOW() " +
            "WHERE id = #{productId} AND sale_status = 'ON_SALE' " +
            "AND #{quantity} > 0 AND stock >= #{quantity} AND deleted = 0")
    int decrementStockIfAvailable(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE product " +
            "SET stock = stock + #{quantity}, update_time = NOW() " +
            "WHERE id = #{productId} AND #{quantity} > 0 AND deleted = 0")
    int incrementStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
