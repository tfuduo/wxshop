package com.dahuntun.wxshop.config;

import com.dahuntun.wxshop.service.ShiroRealmService;
import com.dahuntun.wxshop.service.UserContext;
import com.dahuntun.wxshop.service.UserService;
import com.dahuntun.wxshop.service.VerificationCodeCheckService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
public class ShiroConfig implements WebMvcConfigurer {
    @Autowired
    UserService userService;

    @Value("${wxshop.redis.host}")
    String redisHost;

    @Value("${wxshop.redis.port}")
    int redisPort;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

                response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, POST, DELETE, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type");
                response.setHeader("Access-Control-Allow-Credentials", "true");

                Object tel = SecurityUtils.getSubject().getPrincipal();
                if (tel != null) {
                    userService.getUserByTel(tel.toString()).ifPresent(UserContext::setCurrentUser);
                    return true;
                } else if (Arrays.asList(
                        "/api/code",
                        "/api/login",
                        "/api/status",
                        "/api/logout",
                        "/error"
                ).contains(request.getRequestURI())) {
                    return true;
                } else {
                    response.setStatus(401);
                    return false;
                }
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                UserContext.clearCurrentUser();
            }
        });
    }

    /**
     * 权限拦截器
     * @param securityManager
     * @param shiroLoginFilter
     * @return ShiroFilterFactoryBean shiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, ShiroLoginFilter shiroLoginFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(ShiroRealmService shiroRealmService, RedisCacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(shiroRealmService);
        securityManager.setCacheManager(cacheManager);
        securityManager.setSessionManager(new DefaultWebSessionManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisHost + ":" + redisPort);
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    @Bean
    public ShiroRealmService configShiroRealmService(VerificationCodeCheckService verificationCodeCheckService) {
        return new ShiroRealmService(verificationCodeCheckService);
    }
}
