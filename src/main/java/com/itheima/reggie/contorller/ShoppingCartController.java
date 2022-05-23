package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查询购物车中的信息
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList() {
        Long userId = BaseContext.getId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        if (list == null) {
            return R.error("未查询到数据");
        }
        return R.success(list);
    }

    /**
     * 添加进购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            return R.error("请添加菜品");
        }
        Long userId = BaseContext.getId();
        shoppingCart.setUserId(userId);
        //判断用户添加的是dish还是set meal
        //通过用户的id和菜品的id查询购物车中是否存在同样的菜品或set meal
        //构造用户id查询条件
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);

        Long dishId = shoppingCart.getDishId();
        if (dishId == null) {
            //添加的是set meal
            //构造dishId的查询条件
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        } else {
            //添加的是dish

            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        }

        //查询购物车中是否存在
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if (one == null) {
            //不存在，直接添加
            shoppingCartService.save(shoppingCart);
        } else {
            //存在，则获取其数量并加1
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            //更新
            shoppingCartService.updateById(one);
            shoppingCart = one;
        }

        return R.success(shoppingCart);
    }

    /**
     * 删除购物车中的信息
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> delete(@RequestBody ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            return R.error("未选择数据");
        }
        //先查询购物车中菜品的数量是否大于1,大于1 则进行数量减1 ，如果数量等于1 则删除购物车中的数据
        //获取用户的id
        Long userId = BaseContext.getId();
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        if (dishId == null) {
            //操作的是set meal
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        } else {
            //操作的是dish
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }

        //判断菜品或套餐在购物车中的数量是否大于1
        // int count = shoppingCartService.count(lambdaQueryWrapper);
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer count = one.getNumber();
        if (count > 1) {
            //购物车中数量大于1
            //则数量减一
            Integer number = one.getNumber();
            one.setNumber(number - 1);
            shoppingCartService.updateById(one);

        } else {
            //购物车中数量不大于1
            //则直接删除
            shoppingCartService.remove(lambdaQueryWrapper);
        }


//        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
//        wrapper
//                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
//                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
//        boolean flag = shoppingCartService.remove(wrapper);
//
//        if (!flag) {
//            return R.error("删除失败");
//        }


        return R.success("移除成功");
    }

    /**
     * 清楚当前用户的购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart() {
        Long userId = BaseContext.getId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        shoppingCartService.remove(wrapper);

        return R.success("清空购物车成功");
    }

}
