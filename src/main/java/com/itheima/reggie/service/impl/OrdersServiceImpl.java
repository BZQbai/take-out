package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.OrdersDao;
import com.itheima.reggie.domain.Orders;
import com.itheima.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders> implements OrdersService {
}
