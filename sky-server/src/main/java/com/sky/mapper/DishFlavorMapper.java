package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 插入口味
     * @param dishFlavor
     */
    @Insert("insert into dish_flavor(dish_id, name, value) VALUES (#{dishId},#{name},#{value})")
    void insertDishFlavor(DishFlavor dishFlavor);

    /**
     * 根据菜品id删除口味数据
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteFlavorByDishId(Long dishId);

    /**
     * 批量删除口味
     * @param ids
     */
    void deleteBatch(@Param("ids")List<Long> ids);

    /**
     * 给口味设置菜品id
     * @param dishId
     */
    void setDishId(Long dishId);

    /**
     * 批量插入口味数据
     * @param flavors 口味列表
     */
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
