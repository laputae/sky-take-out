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
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags="菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result saveDish(@RequestBody DishDTO dishDTO){
        log.info("菜品信息: {}",dishDTO);
        dishService.saveDish(dishDTO);
        return Result.success();
    }
    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageDish(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品的信息是: {}",dishPageQueryDTO);
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return  Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary="批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除的菜品的ID: {}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @Operation(summary="菜品起售停售")
    public Result updateStatus(@PathVariable Integer status,@RequestParam Long id){
        log.info("菜品的状态是: {}, id是: {}",status,id);
        dishService.updateStatus(status, id);
        return Result.success();
    }
    @PutMapping
    @Operation(summary = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改的菜品是: {}",dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }
    @GetMapping("/{id}")
    @Operation(tags="根据id查询菜品",description = "可用于修改菜品时的回显")
    public Result<DishVO> getDishById(@PathVariable Long id){
        log.info("查询的菜品id是: {}",id);
        return Result.success(dishService.getByIdWithFlavor(id));
    }
}
