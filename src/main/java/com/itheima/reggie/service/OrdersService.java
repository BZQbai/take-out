package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.Orders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrdersService extends IService<Orders> {

    public void order(Orders orders);
}
