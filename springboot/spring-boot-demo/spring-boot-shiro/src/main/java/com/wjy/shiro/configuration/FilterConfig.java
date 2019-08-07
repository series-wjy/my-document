package com.wjy.shiro.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

@Configuration
public class FilterConfig {

    /**
     * 解决hibernate懒加载出现的no session问题
     * @return
     */
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(new OpenSessionInViewFilter());
//        filterRegistrationBean.addInitParameter("urlPatterns", "/*");
//        return filterRegistrationBean;
//    }

    /**
     * 解决jpa 懒加载出现的no session问题
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new OpenEntityManagerInViewFilter());
        filterRegistrationBean.addInitParameter("urlPatterns", "/*");
        return filterRegistrationBean;
    }
}