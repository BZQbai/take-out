package com.itheima.reggie.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")
@Slf4j
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



    /**
     * 添加员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> addEmployee(HttpServletRequest request ,@RequestBody Employee employee) {
        /**
         * 1.先初始化密码，并进行MD5加密
         * 2.根据传入的username,查询数据库，确保没有重复
         * 3，将对应的信息获取出来
         * 4.根据查询的结果，将数据插入到数据库中
         *
         */
//        1.先初始化密码，并进行MD5加密
        String password = "123456";
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
//        2.根据传入的username,查询数据库，确保没有重复
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        if (emp != null) {
            return R.error("用户名已存在");
        }

        employee.setPassword(password);
        //获取当前的时间
        employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        employee.setUpdateTime(LocalDateTime.now());

        //获取创建人，从session中获取相应的ID
        Long LoginUserID = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(LoginUserID);
        employee.setUpdateUser(LoginUserID);

        //将数据添加进数据库
        employeeService.save(employee);

        return R.success("添加成功");
    }

    /**
     * 分页查询（首页展示）
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Employee> queryPage(Integer page ,Integer pageSize){
        //创建分页对象
        IPage<Employee> pg = new Page<>(page, pageSize);

        return null;
    }

}
