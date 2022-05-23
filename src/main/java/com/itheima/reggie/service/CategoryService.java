package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 删除菜品的分类
     * @param id
     */
    public void removeCategoryById(Long id);

    /**
     * 菜品分类分页展示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Page<Category> getPage(int page,int pageSize ,String name);
}
