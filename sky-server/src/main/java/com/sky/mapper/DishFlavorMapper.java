package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    void deleteByDishId(Long dishId);

    /**
     * 批量删除口味
     * @param ids
     */
    void deleteBatch(@Param("ids")List<Long> ids);
}
