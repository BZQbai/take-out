package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.User;
import com.itheima.reggie.dto.UserDto;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.Message;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.nio.cs.US_ASCII;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 用户登录
     * @param userDto
     * @return
     */
    @PostMapping("/login")
    public R<String> login(HttpServletRequest request, @RequestBody UserDto userDto){
//        String code = userDto.getCode();
//
//        String code1 = request.getSession().getAttribute("code").toString();
//
//        if (!(code.equals(code1))) {
//            return R.error("验证码错误");
//        }
//
//        //判断电话号码是否为空
//        if (!StringUtils.isNotEmpty(userDto.getPhone())) {
//            return R.error("电话号码为空");
//        }
//
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(User::getPhone, userDto.getPhone());
//        User one = userService.getOne(wrapper);
//
//        if (one == null) {
//            userDto.setStatus(1);
//            userService.save(userDto);
//        }
//       // Long id = one.getId();
//        User user1 = userService.getOne(wrapper);
//        request.getSession().setAttribute("userId",user1.getId());

        boolean flag = userService.userLogin(request, userDto);
        if (!flag) {
            return R.error(Message.LOGIN_ERROR);
        }

        return R.success(Message.LOGIN_SUCCESS);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpServletRequest request) {
        // request.getSession().removeAttribute("userId");
        boolean flag = userService.userLogOut(request);
        if (!flag) {
            return R.error(Message.LOG_OUT_ERROR);
        }
        return R.success(Message.LOG_OUT_SUCCESS);
    }

    /**
     * 验证的生成和发送
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/getCode")
    public R<String> sendMessage1(HttpServletRequest request, @RequestBody User user) {

        boolean flag = userService.getCodeMsg(user);

//        if (StringUtils.isEmpty(user.getPhone())) {
//            return R.error("请输入手机号");
//        }
//        Integer integer = ValidateCodeUtils.generateValidateCode(4);
//        SMSUtils.sendMessage(user.getPhone(), String.valueOf(integer));
//        HttpSession session = request.getSession();
//        session.setAttribute("code",integer);
//        log.info(String.valueOf(integer));

        if (!flag) {
            return R.error(Message.CODE_SEND_ERROR);
        }

        return R.success(Message.CODE_SEND_SUCCESS);
    }


}
