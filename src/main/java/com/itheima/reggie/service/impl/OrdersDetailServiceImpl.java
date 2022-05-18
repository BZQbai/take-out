package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.OrdersDetailDao;
import com.itheima.reggie.domain.OrderDetail;
import com.itheima.reggie.service.OrdersDetailService;
import com.itheima.reggie.service.OrdersService;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.stereotype.Service;

@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrdersDetailDao, OrderDetail> implements OrdersDetailService {
}
