package com.proj.framework.shiro.config;

import com.youth.boot.autoconfigure.properties.YouthProperties;
import com.youth.common.tool.Global;
import com.youth.web.framework.platform.shiro.filter.UserUnauthorizedFilter;
import com.youth.web.framework.platform.support.security.csrf.CsrfFilter;
import com.youth.web.system.shiro.filter.InterfacePermissionFilter;
import com.youth.web.system.shiro.filter.IpAndTimePeriodFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({ YouthProperties.class })
public class ShiroConfig {

    private String springApplicationName = Global.getConfig("spring.application.name");

    //注册realm
    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName(String.format("%s_JSESSIONID", springApplicationName));
        simpleCookie.setPath("/");
        simpleCookie.setSecure(false);
        simpleCookie.setHttpOnly(true);
        return simpleCookie;
    }

    @Bean
    public DefaultWebSessionManager defaultWebSessionManager(Cookie simpleCookie) {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionIdCookie(simpleCookie);
        defaultWebSessionManager.setGlobalSessionTimeout(3600000);
        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);
        return defaultWebSessionManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, YouthProperties youthProperties) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("auth", userUnauthorizedFilter());
        filterMap.put("interface", interfacePermissionFilter());
        filterMap.put("ip", ipAndTimePeriodFilter());
        filterMap.put("csrf", csrfFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/page/login");
        shiroFilterFactoryBean.setSuccessUrl("/redirect/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/page/error403");
        Map<String,String> map = new LinkedHashMap<>();
        //登录
        map.put("/login","anon");
        //对外提供接口
        map.put("/api/**","anon");
        //公共页面请求
        map.put("/page/**", "anon");
        //错误页面
        map.put("/error*","anon");
        //开放页面或接口
        map.put("/open/**","anon");
        //静态资源
        map.put("/**/*.html","anon");
        map.put("/**/static/**","anon");
        //微信服务地址配置验证文件路径
        map.put("/*.txt", "anon");
        //swagger接口权限 开放
        if(youthProperties != null && youthProperties.getDebug().getSwagger().isEnabled()) {
            map.put("/swagger-ui.html", "anon");
            map.put("/webjars/**", "anon");
            map.put("/v2/**", "anon");
            map.put("/swagger-resources/**", "anon");
        }

        //所有接口用户认证
        //对所有用户认证
        String t = "auth,ip,interface";
        if (youthProperties != null && youthProperties.getSecurity() != null && youthProperties.getSecurity().getCsrf() != null){
            if(youthProperties.getSecurity().getCsrf().getEnabled()) {
                t += ",csrf";
            }
        }
        map.put("/**", t);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    private UserUnauthorizedFilter userUnauthorizedFilter() {
        return new UserUnauthorizedFilter();
    }

    private InterfacePermissionFilter interfacePermissionFilter() {
        return new InterfacePermissionFilter();
    }

    private Filter ipAndTimePeriodFilter() {
        return new IpAndTimePeriodFilter();
    }

    private Filter csrfFilter() {
        return new CsrfFilter();
    }

}
