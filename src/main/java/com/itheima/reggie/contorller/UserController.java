package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.User;
import com.itheima.reggie.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<String> login(HttpServletRequest request, @RequestBody User user){
        //判断电话号码是否为空
        if (!StringUtils.isNotEmpty(user.getPhone())) {
            return R.error("NOTLOGIN");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, user.getPhone());
        User one = userService.getOne(wrapper);

        if (one == null) {
            userService.save(user);
        }
       // Long id = one.getId();
        User user1 = userService.getOne(wrapper);
        request.getSession().setAttribute("userId",user1.getId());


        return R.success("登录成功");
    }
}
