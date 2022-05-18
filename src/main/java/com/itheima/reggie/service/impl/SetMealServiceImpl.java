package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.SetMealDao;
import com.itheima.reggie.domain.SetMealDish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.dto.SetMealDto;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealDao, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;
    @Override
    public void saveSetMeal(SetMealDto setMealDto) {
        log.info(setMealDto.toString());
        //保存套餐
      //  setMealDto.setCode(String.valueOf(System.currentTimeMillis()));
        this.save(setMealDto);
        //获取套餐的id
        Long setMealId = setMealDto.getId();

        //获取setMealDto对象中的菜品信息
        List<SetMealDish> setmealDishes = setMealDto.getSetmealDishes();
        //将每个菜品信息加上套餐的id
        for (SetMealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setMealId);
        }

        setMealDishService.saveBatch(setmealDishes);

    }

    @Override
    public void editSetMealMessage(SetMealDto setMealDto) {
        //将套餐的信息进行更新
        this.updateById(setMealDto);
        //获取套餐的Id
        Long setMealId = setMealDto.getId();

        //先删除套餐中的菜品信息
        LambdaQueryWrapper<SetMealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetMealDish::getSetmealId, setMealId);
        setMealDishService.remove(wrapper);
        //将修改的数据保存进菜品表中
        List<SetMealDish> setmealDishes = setMealDto.getSetmealDishes();
        //给每个菜品都添加上套餐的id
        for (SetMealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setMealId);
        }

        setMealDishService.saveBatch(setmealDishes);

    }
}
