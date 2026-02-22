package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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


    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);


    void cancel(Integer status, Long id);

    /**
     * 获取待派送的订单数量
     *
     * @return
     */
    Integer getConfirmedOrderNum();

    /**
     * 获取派送中的订单数量
     *
     * @return
     */
    Integer getDeliveryInProgressNum();

    /**
     * 获取待接单数量
     *
     * @return
     */
    Integer getToBeConfirmedNum();


    /**
     * 分页条件查询并按下单时间排序
     *
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     *
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     *
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据状态和下单时间查询订单
     *
     * @param status
     * @param orderTime
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据订单号和用户id查询套餐
     *
     * @param outTradeNo
     * @param userId
     * @return
     */
    @Select("select * from orders where number=#{outTrade} and user_id=#{userId}")
    Orders getByNumberAndUserId(String outTradeNo, Long userId);

    /**
     * 根据动态条件统计营业额
     *
     * @param map
     */
    Double sumByMap(Map map);

    /**
     * 根据条件动态统计订单数据
     *
     * @param map
     * @return
     */
    Integer getOrderCount(Map map);

    /**
     * 查询商品销量排名
     *
     * @param begin
     * @param end
     */
    List<GoodsSalesDTO> getTop10(LocalDateTime begin, LocalDateTime end);

    Integer countByMap(Map map);
}
