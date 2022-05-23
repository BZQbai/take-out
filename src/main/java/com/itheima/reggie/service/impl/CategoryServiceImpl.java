package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.CategoryDao;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.exception.BusinessException;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryService categoryService;
    @Override
    public void removeCategoryById(Long id) {
        //根据Id查询dish菜品中是否存在有菜品是该分类
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId, id);
        List<Dish> list = dishService.list(wrapper);
        //判断该分类中是否存在菜品
        if (list != null && list.size() > 0) {
            throw new BusinessException("删除失败，该分类中存在菜品，不能删除！");
        }

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, id);
        List<Setmeal> list1 = setMealService.list(queryWrapper);
        if (list1 != null && list1.size() > 0) {
            throw new BusinessException("删除失败，该分类中存在套餐，不能删除！");
        }

        categoryService.removeById(id);

    }

    @Override
    public Page<Category> getPage(int page, int pageSize, String name) {
        if (page <= 0 || pageSize <= 0) {
            throw new BusinessException("请正确给出分页参数");
        }
        //创建分页对象
        Page<Category> pg = new Page<>(page, pageSize);
        //增加排序
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        wrapper
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime)
                .eq(name != null, Category::getName, name);
        //查询分页
        Page<Category> page1 = categoryService.page(pg, wrapper);


        return page1;
    }
}
