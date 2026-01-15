package com.sky.mapper;

import com.sky.aspect.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 增加菜品
     *
     * @param dish
     * @return
     */
    @AutoFill(value= OperationType.INSERT)
    @Insert("insert into dish (name,category_id,price,image,description,status,create_time,update_time,create_user,update_user)" + " values" +
            " (#{name},#{categoryId},#{price},#{image},#{description},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void save(Dish dish);

}
