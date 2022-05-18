package com.itheima.reggie.dto;


import com.itheima.reggie.domain.SetMealDish;
import com.itheima.reggie.domain.Setmeal;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetMealDto extends Setmeal {

    private List<SetMealDish> setmealDishes;

    private String categoryName;
}
