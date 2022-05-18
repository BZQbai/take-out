package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.dao.OrdersDao;
import com.itheima.reggie.domain.*;
import com.itheima.reggie.exception.BusinessException;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrdersDetailService ordersDetailService;

    @Override
    public void order(Orders orders) {
        /**
         * 解决思路：
         * 获取session中的用户id，
         * 通过id查询用户的基本信息
         * 通过用户id查询购物车中的基本信息
         * 通过购物车中的菜品id,获取菜品的信息，和口味
         * 将菜品的信息添加进订单明细表中
         *
         */

        //获取用户id
        Long userId = BaseContext.getId();
        //通过用户id获取购物车中的信息
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);

        if (shoppingCartList == null || shoppingCartList.size() <= 0) {
            throw new BusinessException("购物车为空！");
        }

        //获取用户的信息
        User user = userService.getById(userId);

        //获取用户的地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new BusinessException("地址信息有误");
        }

        //创建订单的id
        long orderId = IdWorker.getId();

        //组建订单明细表
        AtomicInteger amount = new AtomicInteger(0);


        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());



        //组建订单表
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAddress(addressBook.getDetail());
        orders.setPhone(user.getPhone());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setAmount(new BigDecimal(amount.get()));


        this.save(orders);

        ordersDetailService.saveBatch(orderDetails);

        shoppingCartService.remove(wrapper);

    }
}
