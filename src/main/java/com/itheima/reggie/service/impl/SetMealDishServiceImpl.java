package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.SetMealDishDao;
import com.itheima.reggie.domain.SetMealDish;
import com.itheima.reggie.service.SetMealDishService;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishDao,SetMealDish> implements SetMealDishService {
}
