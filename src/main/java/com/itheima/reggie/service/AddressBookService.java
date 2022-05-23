package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    /**
     * 根据id删除地址
     * @param ids
     */
    Boolean deleteAddressById(Long ids);

}
