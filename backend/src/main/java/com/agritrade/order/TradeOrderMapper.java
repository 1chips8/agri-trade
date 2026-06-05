package com.agritrade.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeOrderMapper extends BaseMapper<TradeOrder> {
    @Update("UPDATE trade_order SET order_status = 'WAIT_SHIPMENT', pay_time = #{payTime}, update_time = NOW() " +
            "WHERE id = #{orderId} AND user_id = #{userId} AND order_status = 'PENDING_PAYMENT' AND deleted = 0")
    int markPaidIfPending(@Param("orderId") Long orderId,
                          @Param("userId") Long userId,
                          @Param("payTime") LocalDateTime payTime);

    @Update("UPDATE trade_order SET order_status = 'CANCELLED', cancel_reason = #{reason}, " +
            "cancel_time = #{cancelTime}, update_time = NOW() " +
            "WHERE id = #{orderId} AND user_id = #{userId} AND order_status = 'PENDING_PAYMENT' AND deleted = 0")
    int cancelIfPending(@Param("orderId") Long orderId,
                        @Param("userId") Long userId,
                        @Param("reason") String reason,
                        @Param("cancelTime") LocalDateTime cancelTime);

    @Update("UPDATE trade_order SET order_status = 'SHIPPED', ship_time = #{shipTime}, update_time = NOW() " +
            "WHERE id = #{orderId} AND merchant_id = #{merchantId} AND order_status = 'WAIT_SHIPMENT' AND deleted = 0")
    int shipIfWaiting(@Param("orderId") Long orderId,
                      @Param("merchantId") Long merchantId,
                      @Param("shipTime") LocalDateTime shipTime);

    @Update("UPDATE trade_order SET order_status = 'COMPLETED', complete_time = #{completeTime}, update_time = NOW() " +
            "WHERE id = #{orderId} AND user_id = #{userId} AND order_status = 'SHIPPED' AND deleted = 0")
    int completeIfShipped(@Param("orderId") Long orderId,
                          @Param("userId") Long userId,
                          @Param("completeTime") LocalDateTime completeTime);

    @Select("SELECT * FROM trade_order " +
            "WHERE order_status = 'PENDING_PAYMENT' AND create_time <= #{cutoff} AND deleted = 0 " +
            "ORDER BY create_time ASC LIMIT #{limit}")
    List<TradeOrder> selectExpiredPendingOrders(@Param("cutoff") LocalDateTime cutoff,
                                                @Param("limit") int limit);
}
