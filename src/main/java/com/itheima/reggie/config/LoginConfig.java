package com.itheima.reggie.config;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.utils.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
@Slf4j
public class LoginConfig implements Filter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //1.获取请求的路径  /backend/index.html
        String requestURI = request.getRequestURI();
        //给出放行的资源路径
        String[] url = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/getCode"
        };
        for (String s : url) {
            if (PATH_MATCHER.match(s, requestURI)) {
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
        }

        //判断用户是否登录
        Long employee = (Long) request.getSession().getAttribute("employee");
        if (employee!=null){
            BaseContext.setId(employee);
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        //判断手机用户是否登录
        Long userId = (Long) request.getSession().getAttribute(Message.USER_ID);
        if (userId!=null) {
            BaseContext.setId(userId);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //相应回未登录的信息
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;



    }
}
