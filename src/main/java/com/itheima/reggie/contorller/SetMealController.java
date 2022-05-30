package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dao.SetMealDishDao;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.SetMealDish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.dto.SetMealDto;
import com.itheima.reggie.exception.BusinessException;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 添加套餐
     *
     * @param setMealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setMeal",allEntries = true)
    public R<String> saveSetMealDish(@RequestBody SetMealDto setMealDto) {
        setMealService.saveSetMeal(setMealDto);

        return R.success("套餐添加成功");
    }

    /**
     * 套餐的分页展示
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetMealDto>> getPage(Integer page, Integer pageSize, String name) {
        //创建一个分页对象
        Page<Setmeal> pg = new Page<>(page, pageSize);
        //创建一个setmealdto对象
        Page<SetMealDto> dtoPage = new Page<>();
        //条件查询
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(name != null, Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        Page<Setmeal> page1 = setMealService.page(pg, wrapper);
        if (page1 == null && page1.getRecords().size() <= 0) {
            throw new BusinessException("未查询到数据");
        }

        //将pg对象中的内容copy到dtopage中，并排除records
        BeanUtils.copyProperties(pg, dtoPage, "records");

        //获取pg对象中查询到的dish信息
        List<Setmeal> records = pg.getRecords();
        //通过流将dish的分类名称查询出来
        List<SetMealDto> collect = records.stream().map((item) -> {
            //创建一个setmealDto对象，用于接受dish的信息
            SetMealDto setMealDto = new SetMealDto();
            //将dish遍历的每个dish信息copy到setMealDto中
            BeanUtils.copyProperties(item, setMealDto);
            //获取dish分类的Id
            Long categoryId = item.getCategoryId();
            //根据id查询dish分类对象
            Category category = categoryService.getById(categoryId);
            //判断对象是否为空
            if (category != null) {
                String categoryName = category.getName();
                setMealDto.setCategoryName(categoryName);
            }
            return setMealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(collect);

        return R.success(dtoPage);
    }

    /**
     * 根据id查询set meal message
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetMealDto> getSetMealById(@PathVariable("id") Long id) {
        //创建一个setMealdto对象
        SetMealDto setMealDto = new SetMealDto();
        //查询set meal 的基本 message
        Setmeal setmeal = setMealService.getById(id);
        //查询套餐分类的名称
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //获取菜品分类的id
        Long categoryId = setmeal.getCategoryId();
        Category category = categoryService.getById(categoryId);
        String categoryName = category.getName();
        //获取套餐的菜品message
        LambdaQueryWrapper<SetMealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMealDish::getSetmealId, id);
        List<SetMealDish> list = setMealDishService.list(queryWrapper);

        setMealDto.setSetmealDishes(list);
        setMealDto.setCategoryName(categoryName);
        BeanUtils.copyProperties(setmeal, setMealDto);

        return R.success(setMealDto);
    }

    /**
     * 修改菜品的信息
     *
     * @param setMealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setMeal",allEntries = true)
    public R<String> editSetMeal(@RequestBody SetMealDto setMealDto) {
        setMealService.editSetMealMessage(setMealDto);
        return R.success("修改成功");
    }

    /**
     * 修改售卖状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setMeal",allEntries = true)
    public R<String> editStatus(@PathVariable("status") int status, Long[] ids) {
        if (ids == null && ids.length <= 0) {
            throw new BusinessException("请选择要修改的套餐");
        }
        for (Long id : ids) {
            LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Setmeal::getId, id);
            Setmeal setmeal = new Setmeal();
            setmeal.setStatus(status);

            boolean flag = setMealService.update(setmeal, wrapper);

            if (!flag) {
                throw new BusinessException("修改失败");
            }
        }
        return R.success("修改成功");

    }


    @DeleteMapping
    @Transactional
    @CacheEvict(value = "setMeal",allEntries = true)
    public R<String> deletedSetMeal(Long[] ids) {
        if (ids == null && ids.length <= 0) {
            throw new BusinessException("请选择要删除的套餐");
        }
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        int count = setMealService.count(lqw);
        if (count > 0) {
            return R.error("套餐售卖中，不能删除");
        }

        for (Long id : ids) {
            LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Setmeal::getId, id);
            //先判断套餐内是否存在菜品的信息
            LambdaQueryWrapper<SetMealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetMealDish::getSetmealId, id);
            List<SetMealDish> list = setMealDishService.list(queryWrapper);
            //套餐内不存在菜品
            if (list == null && list.size() <= 0) {
                setMealService.remove(wrapper);
            }
            //如果存在菜品信息，也一起删除
            setMealDishService.remove(queryWrapper);
            setMealService.remove(wrapper);

        }

        return R.success("删除成功");
    }

    /**
     * user查询套餐的信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setMeal",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> getSetMealList(Setmeal setmeal) {
        //检查参数
        if (setmeal == null) {
            return R.error("请检查参数！");
        }
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> list = setMealService.list(wrapper);
        if (list == null && list.size() <= 0) {
            return R.error("未查询到数据");
        }
        return R.success(list);

    }

    /**
     * 展示套餐的信息
     * @param setMealId
     * @return
     */
    @GetMapping("/dish/{setMealId}")
    @Cacheable(value = "setMeal",key = "'setMealDish:'+#setMealId")
    public R<List<SetMealDish>> getSetMealDtoList(@PathVariable("setMealId") Long setMealId) {
        //检查参数
        if (setMealId == null) {
            return R.error("请检查参数！");
        }
        //获取套餐的信息
       // Setmeal setmeal = setMealService.getById(setMealId);
        //根据套餐的id,获取套餐内的菜品信息
        LambdaQueryWrapper<SetMealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetMealDish::getSetmealId, setMealId);
        List<SetMealDish> list = setMealDishService.list(wrapper);
        //获取每个菜品的图片
        List<SetMealDish> collect = list.stream().map((item) -> {
            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);
            String image = dish.getImage();
            item.setImage(image);
            return item;
        }).collect(Collectors.toList());


        //获取每个菜品的图片
        /*List<SetMealDish> collect = list.stream().map((item) -> {
            LambdaQueryWrapper<SetMealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.groupBy(SetMealDish::getDishId).eq(SetMealDish::getDishId,item.getDishId());
            SetMealDish setMealDish = new SetMealDish();
            BeanUtils.copyProperties(list, setMealDish);
            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);
            String image = dish.getImage();
            setMealDish.setImage(image);
            setMealDish.setName(dish.getName());
            int count = setMealDishService.count(queryWrapper);
            setMealDish.setCopies(count);
            setMealDish.setPrice(BigDecimal.valueOf(count).multiply(dish.getPrice()));
            count = 0;
            return setMealDish;
        }).collect(Collectors.toList());*/


//        SetMealDto setMealDto = new SetMealDto();
//        BeanUtils.copyProperties(setmeal, setMealDto);
//        setMealDto.setSetmealDishes(list);

        return R.success(collect);
    }

}
