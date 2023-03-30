package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
//    路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        获取请求URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求 {}",requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "employee/logout",
                "/backend/**",
                "/frontend/**"
        };

//        判断是否需要处理
        boolean check = check(urls,requestURI);

//        如果不需要处理，直接放行
        if(check){
            log.info("本次请求 {}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        如果已登录直接放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录,用户id为: {}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
//        如果未登录
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

//    检查是否放行
//    @param urls
//    @param requestURI
//    @return
    public boolean check(String[] urls,String requestURI){
        for (String url: urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
