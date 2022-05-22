package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.OrderDetail;
import com.itheima.reggie.domain.Orders;
import com.itheima.reggie.domain.ShoppingCart;
import com.itheima.reggie.service.OrdersDetailService;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.ShoppingCartService;
import com.sun.org.apache.bcel.internal.generic.LADD;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.xml.internal.stream.StaxErrorReporter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersDetailService ordersDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 订单提交
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> addOrder(@RequestBody Orders orders) {
        ordersService.order(orders);
        return R.success("订单提交成功");
    }

    /**
     * 订单分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> ordersPage(Integer page, Integer pageSize, HttpServletRequest request) {
        Page<Orders> pg = new Page<>(page, pageSize);
        Long userId = (Long) request.getSession().getAttribute("userId");
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        wrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pg,wrapper);
        return R.success(pg);
    }

    /**
     * 后台订单处理
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> ordersPageHandle(Integer page, Integer pageSize, Long number, String beginTime, String endTime) {
        Page<Orders> pg = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(number != null, Orders::getId, number);
        wrapper.between(beginTime != null, Orders::getCheckoutTime, beginTime, endTime);
        wrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(pg, wrapper);
        return R.success(pg);
    }

    /**
     * 修改订单的状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> editStatus(@RequestBody Orders orders) {
        if (orders == null) {
            R.error("修改失败");
        }
        ordersService.updateById(orders);
        return R.success("修改成功");
    }

    /**
     * 再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> againOrder(@RequestBody Orders orders) {
        //通过订单号查询订单表中该订单的信息
        Long ordersId = orders.getId();
//        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
//
//        wrapper.eq(Orders::getNumber, ordersId);
//        Orders orders1 = ordersService.getById(ordersId);
//        orders1.setStatus(2);

        //通过获取订单的信息
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, ordersId);
        List<OrderDetail> list = ordersDetailService.list(wrapper);
        Long id = BaseContext.getId();
        List<ShoppingCart> collect = list.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(item, shoppingCart);
            shoppingCart.setUserId(id);
            return shoppingCart;
        }).collect(Collectors.toList());

//        shoppingCart.setAmount(orderDetail.getAmount());
//        shoppingCart.setCreateTime(LocalDateTime.now());
//        shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
//        shoppingCart.setDishId(orderDetail.getDishId());
//        shoppingCart.setSetmealId(orderDetail.getSetmealId());
//        shoppingCart.setNumber(orderDetail.getNumber());
//        shoppingCart.setName(orderDetail.getName());
//        shoppingCart.setImage(orderDetail.getImage());
       // BeanUtils.copyProperties(list, list1);

        shoppingCartService.saveBatch(collect);
        return R.success("再来一单成功");
    }


}
