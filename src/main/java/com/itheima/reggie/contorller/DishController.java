package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto) {
        log.info(String.valueOf(dishDto));
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }


    /**
     * 菜品分页加条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize, String name) {
        //创建分页对象
        Page<Dish> pg = new Page<>(page, pageSize);
        Page<DishDto> pgDto = new Page<>();
        //添加过滤条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);

        Page<Dish> page1 = dishService.page(pg, wrapper);
        //将page1对象中的数据拷贝到pgDto,并排除records
        BeanUtils.copyProperties(pg, pgDto, "records");
        //获取分页查询的数据
        List<Dish> records = pg.getRecords();

        //使用流 将records中的categoryId的名称查询出来，并赋值给dishDto对象的categoryName
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将每一个对象的值都拷贝给新的dishDto对象
            BeanUtils.copyProperties(item, dishDto);
            //获取菜品分类的Id
            Long categoryId = item.getCategoryId();
            //根据Id查询菜品
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //获取菜品的名称
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //将转化的记录 赋值给分页对象
        pgDto.setRecords(list);

       /* //循环遍历集合
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            Long categoryId = record.getCategoryId();
            //获取每个菜品分类对象
            Category category = categoryService.getById(categoryId);
            //获取菜品分类的名称
            String categoryName = category.getName();
            //将菜品分类的名称加入到dishDto对象中
            dishDto.setCategoryName(categoryName);

        }*/

        if (page1 == null) {
            return R.error("服务器错误");
        }

        return R.success(pgDto);
    }


    /**
     * 根据id获取对应的菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") Long id) {
        DishDto dishDto = new DishDto();
        //获取一个dish对象
        Dish dish = dishService.getById(id);
        //获取口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);

        //获取菜品分类名称
        Long categoryId = dish.getCategoryId();
        Category category = categoryService.getById(categoryId);
        String categoryName = category.getName();

        dishDto.setCategoryName(categoryName);
        dishDto.setFlavors(dishFlavors);

        BeanUtils.copyProperties(dish,dishDto);

        return R.success(dishDto);

    }

    @PutMapping
    public R<String> editDish(@RequestBody DishDto dishDto) {

        dishService.editDish(dishDto);

        return R.success("修改成功");

    }

}
