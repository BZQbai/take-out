package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Employee;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request ,@RequestBody Employee employee){
        // 1.接受用户传递的参数，并将其密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        //2.根据用户名进行查询
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        //3.判断是否数据库中是否存在该用户
        if (emp == null) {
            return R.error("登录失败，用户不存在");
        }

        //4.判断密码是否正确
        if (!(password.equals(emp.getPassword()))){
            return R.error("登录失败，密码错误");
        }

        //5.判断用户的状态是否禁用
        if (emp.getStatus() == 0) {
            return R.error("账号被禁用");
        }
        //6.将用户的id存入到session中
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logOut(HttpServletRequest request){
        //清空session中的数据
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

}
