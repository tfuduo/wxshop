package com.dahuntun.wxshop.intercpetor;

import com.dahuntun.wxshop.service.UserContext;
import com.dahuntun.wxshop.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UserLoginInterceptor implements HandlerInterceptor {

    private UserService userService;

    public UserLoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object tel = SecurityUtils.getSubject().getPrincipal();
        if (tel != null) {
            //说明已经登录了
            userService.getUserByTel(tel.toString()).ifPresent(UserContext::setCurrentUser);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //线程会被复用，若不清理线程信息，下次该线程再用来处理用户请求时会出现“串号”的情况
        UserContext.setCurrentUser(null);
    }
}
