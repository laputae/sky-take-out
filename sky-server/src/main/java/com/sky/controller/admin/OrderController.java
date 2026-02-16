package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "商家管理订单接口")
public class OrderController {
    @Autowired
    OrderService orderService;

    @ApiOperation("商家根据订单id查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        log.info("商家查询订单详情，订单id是: {}", id);
        return Result.success(orderService.getOrderDetail(id));
    }

    @ApiOperation("搜索订单")
    @GetMapping("/conditionSearch")
    public Result<PageResult> search(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("搜索的订单是: {}", ordersPageQueryDTO);
        return Result.success(orderService.search(ordersPageQueryDTO));
    }

    @GetMapping("/statistics")
    @ApiOperation("统计各个状态的订单数量")
    public Result<OrderStatisticsVO> getStatistics(){
        log.info("统计各个状态的订单数量");
        return Result.success(orderService.statistics());
    }
}
