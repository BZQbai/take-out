package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 删除菜品的分类
     * @param id
     */
    public void removeCategoryById(Long id);
}