package com.wjy.spring.bean.definition;

import com.wjy.spring.bean.factory.DefaultUserFactory;
import com.wjy.spring.bean.factory.UserFactory;
import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Bean 初始化 Demo
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月18日 23:22:00
 */
@Configuration
public class BeanInitializationDemo {
    @Autowired
    private User user;
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(BeanInitializationDemo.class);
        applicationContext.refresh();
        // 非延迟初始化在 spring 应用上下文启动完成后，被初始化
        System.out.println("Spring 应用上下文已启动...");
        applicationContext.getBean(UserFactory.class);
        System.out.println("Spring 应用上下文准备关闭...");
        applicationContext.close();
        System.out.println("Spring 应用上下文关闭成功...");
    }

    @Bean(initMethod = "initMethod", destroyMethod = "customDestroy")
    @Lazy(value = false)
    public UserFactory userFactory() {
        return new DefaultUserFactory();
    }

    @Bean
    public User user() {
        return new User();
    }
}
