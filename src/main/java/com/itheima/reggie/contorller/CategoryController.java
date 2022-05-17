package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Employee;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品的分类
     *
     * @param request
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(HttpServletRequest request, @RequestBody Category category) {
        //获取当前时间
        //  category.setCreateTime(LocalDateTime.now());
        //  category.setUpdateTime(LocalDateTime.now());
        //获取当前操作人
        //  String uId = (String) request.getSession().getAttribute("employee");

        //category.setCreateUser(uId);
        //category.setUpdateUser(uId);

        //调用保存方法

        boolean flag = categoryService.save(category);

        if (!flag) {
            return R.error("添加失败");
        }


        return R.success("添加成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping("/page")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public R<Page<Category>> getPage(Integer page, Integer pageSize) {
        //创建分页对象
        Page<Category> pg = new Page<>(page, pageSize);
        //增加排序
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        wrapper.orderByAsc(Category::getSort);
        //查询分页
        Page<Category> page1 = categoryService.page(pg, wrapper);
        if (page1.getRecords().size() <= 0) {
            return R.error("未查询到数据");
        }

        return R.success(page1);
    }

    /**
     * 修改菜品分类的信息
     *
     * @param request
     * @param category
     * @return
     */
    @PutMapping
    public R<String> editCategory(HttpServletRequest request, @RequestBody Category category) {
        //获取当前时间
        //category.setUpdateTime(LocalDateTime.now());
        //获取当前的操作者
        // String uId = (String) request.getSession().getAttribute("employee");
        // category.setUpdateUser(uId);

        boolean flag = categoryService.updateById(category);
        if (!flag) {
            return R.error("修改未成功");
        }
        return R.success("修改成功");

    }

    /**
     * 根据Id删除菜品的分类
     *
     * @param id
     * @return
     */
    @DeleteMapping()
    public R<String> deleteById(String id) {
        boolean flag = categoryService.removeById(id);
        if (!flag) {
            return R.error("删除失败");
        }
        return R.success("删除成功");
    }

    /**
     * 查询菜品的分类
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    public R<List> findCategoryList(Integer type) {
        //设置查询条件
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getType, type);

        List<Category> list = categoryService.list(wrapper);

        if (list != null && list.size() > 0) {
            return R.success(list);
        }
        return R.error("未查询到菜品的分类");
    }


}
