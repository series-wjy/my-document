package com.wjy.spring.bean.factory;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 默认工厂实现
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月12日 23:07:00
 */
public class DefaultUserFactory implements UserFactory, InitializingBean {

    @PostConstruct
    public void postConstruct() {
        System.out.println("@PostConstruct 方式正在初始化...");
    }

    public void initMethod() {
        System.out.println("initMethod 方式初始化...");
    }

    @Override
    public User createUser() {
        User user = new User();
        user.setId(10L);
        user.setName("工厂生产隔壁老王");
        return user;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean#afterPropertiesSet() 方式初始化...");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("@PreDestroy 销毁方法开始执行...");
    }

    public void customDestroy() {
        System.out.println("自定义 customDestroy() 销毁方法开始执行...");
    }

//    @Override
//    public void destroy() throws Exception {
//        System.out.println("DisposableBean#destroy() 销毁方法开始执行...");
//    }
}
