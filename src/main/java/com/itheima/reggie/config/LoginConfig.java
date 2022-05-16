package com.itheima.reggie.config;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
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
                "/front/**"
        };
        for (String s : url) {
            if (PATH_MATCHER.match(s, requestURI)) {
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
        }

        //判断用户是否登录
        Object employee = request.getSession().getAttribute("employee");
        if (employee!=null){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        //相应回为登录的信息
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;



    }
}
