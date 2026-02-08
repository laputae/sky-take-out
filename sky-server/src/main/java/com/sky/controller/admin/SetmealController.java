package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Api(tags = "管理端-套餐接口")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐: {}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增套餐")
    public Result insert(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐: {}", setmealDTO);
        setmealService.insert(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐: {}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据套餐id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("查询的套餐id是: {}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @PostMapping("status/{status}")
    @ApiOperation("设置套餐起售停售")
    public Result setStatus(Long id, @PathVariable Integer status) {
        log.info("套餐的id是: {}, 状态是: {}", id, (status == 1 ? "起售" : "停售"));
        setmealService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量套餐")
    public Result deleteBatch(@RequestParam List<Long> ids){
        log.info("批量删除套餐的id是: {}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}

