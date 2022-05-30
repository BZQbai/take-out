package com.itheima.reggie.utils;

import com.alibaba.druid.support.spring.stat.annotation.Stat;

public class Message {

    public static final Integer DISH_REDIS_TIMEOUT = 30;

    public static final String USER_ID = "userID";
    public static final String CODE_SEND_SUCCESS = "验证码发送成功";
    public static final String CODE_SEND_ERROR = "验证码发送失败";
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String LOGIN_ERROR = "登录失败";
    public static final String LOG_OUT_ERROR = "退出失败";
    public static final String LOG_OUT_SUCCESS  = "成功退出";

    public static final String SYSTEM_ERROR = "服务器异常，请稍后再试！";

    public static final String USER_PHONE_OR_CODE_NULL ="请输入手机号或验证码";
    public static final String USER_PHONE_NULL ="请输入手机号";


}
