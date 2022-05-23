package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.AddressBookDao;
import com.itheima.reggie.domain.AddressBook;
import com.itheima.reggie.domain.Orders;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {

    @Autowired
    private OrdersService ordersService;
    @Override
    public Boolean deleteAddressById(Long ids) {

        /**
         * 判断id是否关联过订单
         * 如果关联订单，则不能删除
         * 如果没有关联订单，则可以删除
         */

        //根据地址id查询是否存在订单的信息
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getAddressBookId, ids);
        int count = ordersService.count(wrapper);
        if (count > 0) {
           //有关联信息不能删除
            return false;
        }
        //可以删除
        this.removeById(ids);
        return true;
    }
}
