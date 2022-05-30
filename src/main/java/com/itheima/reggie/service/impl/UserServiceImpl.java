package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.UserDao;
import com.itheima.reggie.domain.User;
import com.itheima.reggie.dto.UserDto;
import com.itheima.reggie.exception.BusinessException;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.Message;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码，并将验证码存入redis中
     *
     * @param user
     * @return
     */
    @Override
    public boolean getCodeMsg(User user) {
        //检查传入的参数，手机号为空则抛出异常
        if (StringUtils.isEmpty(user.getPhone())) {
            throw new BusinessException(Message.USER_PHONE_NULL);
        }

        try {
            //利用工具生成验证码
            Integer integer = ValidateCodeUtils.generateValidateCode(4);
            log.info(String.valueOf(integer));
            //发送验证码
            SMSUtils.sendMessage(user.getPhone(), String.valueOf(integer));
            //将验证码存入到redis中
            redisTemplate.opsForValue().set(user.getPhone(), integer, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            return false;
            // throw new RuntimeException(e);
        }
    }

    /**
     * 用户登录功能
     * @param request
     * @param userDto
     * @return
     */
    @Override
    public boolean userLogin(HttpServletRequest request, UserDto userDto) {
        //检查传递的参数
        if (StringUtils.isEmpty(userDto.getCode()) || StringUtils.isEmpty(userDto.getPhone())) {
            throw new BusinessException(Message.USER_PHONE_OR_CODE_NULL);
        }
        //获取用户填入的验证码
        String userCode = userDto.getCode();
        //获取产生的验证码
        Integer code = (Integer) redisTemplate.opsForValue().get(userDto.getPhone());

        //验证验证码的正确性
        if (!userCode.equalsIgnoreCase(String.valueOf(code))) {
            return false;
        }

        try {
            //封装查询条件
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, userDto.getPhone());
            //查询用户信息
            User user = this.getOne(wrapper);
            // 判断用户是否存在
            if (user == null) {
                // 不存在则将用户添加进数据库
                //设置用户的状态
                userDto.setStatus(1);
                //将新用户的信息copy到user对象中
                BeanUtils.copyProperties(userDto,user);
                this.save(user);
            }

            //TODO 存在则直接返回登录成功的信息

            //  将用户的信息存入到session中
            request.getSession().setAttribute(Message.USER_ID, user.getId());

            return true;
        } catch (Exception e) {
            throw new BusinessException(Message.SYSTEM_ERROR);
        }finally {
            //删除redis中的验证码的信息
            redisTemplate.delete(userDto.getPhone());
        }
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @Override
    public boolean userLogOut(HttpServletRequest request) {
        //删除session中的用户信息
        try {
            request.getSession().removeAttribute(Message.USER_ID);
            return true;
        } catch (Exception e) {
            return false;
        }


    }



}
