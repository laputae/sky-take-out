package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据套餐id删除套餐菜品关联关系
     * @param setmealId
     * @return
     */
    int deleteBySetmealId(Long setmealId);

    /**
     * 批量插入套餐菜品关系
     * @param setmealDishes
     * @return
     */
    int insertBatch(List<SetmealDish> setmealDishes);
}
