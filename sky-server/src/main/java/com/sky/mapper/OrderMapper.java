package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     *
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单id获取订单数据
     *
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);


    void cancel(Integer status, Long id);

    /**
     * 获取待派送的订单数量
     * @return
     */
    Integer getConfirmedOrderNum();

    /**
     * 获取派送中的订单数量
     * @return
     */
    Integer getDeliveryInProgressNum();

    /**
     * 获取待接单数量
     * @return
     */
    Integer getToBeConfirmedNum();

    @Update("update orders set status=3 where id=#{id}")
    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    @Update("update orders set rejection_reason=#{rejectionReason} where id=#{id}")
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    @Update("update orders set cancel_reason=#{cancelReason}, status=6 where id=#{id}")
    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);
}
