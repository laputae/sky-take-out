package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务实现
 */
@Transactional
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealService setmealService;

    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    /**
     * 更新套餐
     *
     * @param setmealDTO
     */
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }
        setmealMapper.update(setmeal);
    }


    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @Override
    public void insert(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 分页查询套餐
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<SetmealVO> pageResult = setmealMapper.page(dto);
        for (SetmealVO setmealVO : pageResult) {
            List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishById(setmealVO.getId());
            setmealVO.setSetmealDishes(setmealDishes);
        }
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    /**
     * 根据套餐id查寻套餐
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishById(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 设置套餐的状态为起售或停售
     *
     * @param
     * @return
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishById(id);
        for (SetmealDish setmealDish : setmealDishes) {
            Integer dishStatus = dishMapper.getStatus(setmealDish.getDishId());
            if (dishStatus == 0 && status == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.updateStatus(setmeal);
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     * @return
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        List<String> activeNames = setmealMapper.selectActiveNamesByIds(ids);

        if (activeNames != null && !activeNames.isEmpty()) {
            String names = String.join(", ", activeNames);
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE + ": " + names);
        }
        for (Long id : ids) {
            setmealDishMapper.deleteBySetmealId(id);
        }
        setmealMapper.deleteBatch(ids);
    }
}