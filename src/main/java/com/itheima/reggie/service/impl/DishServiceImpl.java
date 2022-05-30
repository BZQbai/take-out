package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.DishDao;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.exception.BusinessException;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.utils.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.awt.windows.WWindowPeer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

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

        //删除redis中该分类的菜品信息
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();

        redisTemplate.delete(key);

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
        //删除dish_类所有的缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

    }

    /**
     * 查询分类下的菜品信息
     * @param dish
     * @return
     */
    @Override
    public List<DishDto> getDishList(Dish dish) {
        //检查参数
        if (dish == null) {
            throw new BusinessException("请检查传递的参数");
        }

        // 构建key
        String key ="dish_"+dish.getCategoryId()+"_"+dish.getStatus() ;
        //从redis中查询，存在则直接返回，不存在则查询数据库
        List<DishDto> redisCategoryDish = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (redisCategoryDish != null && redisCategoryDish.size() > 0) {
            //存在
            return redisCategoryDish;
        }
        try {
            //不存在

            //封装查询条件
            LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
            wrapper
                    .eq(Dish::getCategoryId, dish.getCategoryId())
                    .eq(dish.getStatus()!=null, Dish::getStatus, dish.getStatus());
            //查询菜品信息
            List<Dish> list = this.list(wrapper);

            //将菜品信息转化为dishDto
            List<DishDto> dishDtoList = list.stream().map((item) -> {
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(item, dishDto);

                Long categoryId1 = item.getCategoryId();
                Category category = categoryService.getById(categoryId1);
                if (category != null) {
                    dishDto.setCategoryName(category.getName());

                }
                //取出每个菜品的id
                Long dishId = item.getId();
                LambdaQueryWrapper<DishFlavor> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(DishFlavor::getDishId, dishId);

                List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper1);

                dishDto.setFlavors(dishFlavors);
                return dishDto;

            }).collect(Collectors.toList());

            //将查出的菜品信息 存入redis
            redisTemplate.opsForValue().set(key,dishDtoList, Message.DISH_REDIS_TIMEOUT, TimeUnit.MINUTES);
            return dishDtoList;
        } catch (Exception e) {
            throw new BusinessException(Message.SYSTEM_ERROR);
        }
    }
}
