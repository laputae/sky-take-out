package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.UserMapper;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * 根据时间区间统计营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        List<String> xList = new ArrayList<>(); // 存储横坐标（日期或小时）
        List<Double> yList = new ArrayList<>(); // 存储纵坐标（营业额）

        // 场景 A：起止日期相同，按小时统计
        if (begin.equals(end)) {
            for (int i = 0; i < 24; i++) {
                // 构造小时边界，例如 14:00:00 到 14:59:59
                LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.of(i, 0, 0));
                LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.of(i, 59, 59, 999_999_999));

                Double turnover = getSumByTime(beginTime, endTime);
                xList.add(i + "点"); // 横坐标显示为 "0点", "1点"...
                yList.add(turnover);
            }
        }
        // 场景 B：日期范围，按天统计
        else {
            LocalDate temp = begin;
            while (!temp.isAfter(end)) {
                LocalDateTime beginTime = LocalDateTime.of(temp, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(temp, LocalTime.MAX);

                Double turnover = getSumByTime(beginTime, endTime);
                xList.add(temp.toString());
                yList.add(turnover);
                temp = temp.plusDays(1);
            }
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(xList, ","))
                .turnoverList(StringUtils.join(yList, ","))
                .build();
    }

    // 提取共用的查询私有方法
    private Double getSumByTime(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("status", Orders.COMPLETED);
        map.put("begin", begin);
        map.put("end", end);
        Double turnover = orderMapper.sumByMap(map);
        return (turnover == null ? 0.0 : turnover);
    }

    /**
     * 根据时间区间统计用户数量
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer newUser = getUserCount(beginTime, endTime);
            Integer totalUser = getUserCount(null, endTime);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();

    }

    private Integer getUserCount(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        return userMapper.countByMap(map);
    }

    /**
     * 订单数据统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> totalOrderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer tempTotalOrderCount = getOrderCount(beginTime, endTime, null);
            Integer tempValidOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            totalOrderCountList.add(tempTotalOrderCount);
            validOrderCountList.add(tempValidOrderCount);
        }
        //时间区间内的总订单数
        Integer totalOrderCount = totalOrderCountList.stream().reduce(Integer::sum).get();
        //时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = 1.0 * validOrderCount / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.getOrderCount(map);
    }

    /**
     * 查询商品销量排名
     *
     * @param begin
     * @param end
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {

            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
            List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getTop10(beginTime, endTime);
            String nameList=StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).toArray(),",");
            String numList=StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).toArray(),",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numList)
                .build();
    }
}
