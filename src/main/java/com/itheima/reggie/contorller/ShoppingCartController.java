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
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList(){
        List<ShoppingCart> list = shoppingCartService.list();
        if (list == null) {
            return R.error("未查询到数据");
        }
        return R.success(list);
    }

    /**
     * 添加进购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            return R.error("请添加菜品");
        }
        Long userId = BaseContext.getId();
        shoppingCart.setUserId(userId);
        boolean flag = shoppingCartService.save(shoppingCart);
        if (!flag) {
            return R.error("添加失败");
        }

        return R.success("添加成功");
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

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        boolean flag = shoppingCartService.remove(wrapper);

        if (!flag) {
            return R.error("删除失败");
        }

        return R.success("移除成功");
    }

    /**
     * 清楚当前用户的购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart(){
        Long userId = BaseContext.getId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        shoppingCartService.remove(wrapper);

        return R.success("清空购物车成功");
    }

}
