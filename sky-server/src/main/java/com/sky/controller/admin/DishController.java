package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result saveDish(@RequestBody DishDTO dishDTO) {
        log.info("菜品信息: {}", dishDTO);
        dishService.insertDishWithFlavor(dishDTO);
        String redisKey = "dish_list_" + dishDTO.getCategoryId();
        //删除redis中缓存的菜品列表
        deleteRedisCache(redisKey);
        return Result.success();
    }

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageDish(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品的信息是: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除的菜品的ID: {}", ids);
        dishService.deleteBatch(ids);
        deleteRedisCache("dish_list_*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result updateStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("菜品的状态是: {}, id是: {}", status, id);
        dishService.updateStatus(status, id);
        deleteRedisCache("dish_list_*");
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改菜品和菜品的口味")
    public Result updateDishWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("修改的菜品是: {}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        //删除redis中缓存的所有菜品和相关的口味信息
        deleteRedisCache("dish_list_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品和菜品口味,用于修改菜品时的回显")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("查询的菜品id是: {}", id);
        return Result.success(dishService.getByIdWithFlavor(id));
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId) {
        log.info("分类id是: {}", categoryId);
        return Result.success(dishService.getByCategoryId(categoryId));
    }

    private void deleteRedisCache(String pattern){
        Set redisKeys = redisTemplate.keys(pattern);
        redisTemplate.delete(redisKeys);
    }
}
