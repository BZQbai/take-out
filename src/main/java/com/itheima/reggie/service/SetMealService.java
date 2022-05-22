package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.dto.SetMealDto;
import org.springframework.transaction.annotation.Transactional;

import java.security.acl.LastOwnerException;
import java.util.List;

@Transactional
public interface SetMealService extends IService<Setmeal> {
    //保存套餐和菜品关系
    public void saveSetMeal(SetMealDto setMealDto);

    //修改套餐的message
    public void editSetMealMessage(SetMealDto setMealDto);

    //删除套餐
    public void deleteSetMeal(List<Long> ids);


}
