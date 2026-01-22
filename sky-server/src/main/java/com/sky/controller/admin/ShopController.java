package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺操作接口")
public class ShopController {
    @Autowired
    RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    @ApiOperation("设置营业状态")
    @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("店铺的状态是: {}", status == 1 ? "营业中" : "暂停营业");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("店铺营业状态是: {}", status == 1 ? "营业中" : "暂停营业");
        return Result.success(status);
    }
}
