package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.DishDao;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.windows.WWindowPeer;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
     //   dishDto.setCode(String.valueOf(System.currentTimeMillis()));
        //保存到菜品表
        this.save(dishDto);
        //获取菜品的Id
        Long dishId = dishDto.getId();
        //遍历口味表 给每个口味加上相应的菜品Id
        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        //将口味保存到菜品表中去
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public void editDish(DishDto dishDto) {
        this.updateById(dishDto);

        Long dishId = dishDto.getId();

        //先删除口味表中的数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(wrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        dishFlavorService.saveBatch(flavors);

    }
}
