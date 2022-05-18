package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.dto.DishDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DishService extends IService<Dish> {
    //保存菜品
    public void saveWithFlavor(DishDto dishDto);
    //编辑菜品
    public void editDish(DishDto dishDto);


}
