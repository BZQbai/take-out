package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.domain.User;
import com.itheima.reggie.dto.UserDto;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {

    //发送验证码
    public boolean getCodeMsg( User user);

    //用户登录
    boolean userLogin(HttpServletRequest request, UserDto userDto);

    //用户退出登录
    boolean userLogOut(HttpServletRequest request);
}
