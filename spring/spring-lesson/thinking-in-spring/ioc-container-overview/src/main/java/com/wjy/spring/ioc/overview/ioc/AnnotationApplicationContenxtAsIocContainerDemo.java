package com.wjy.spring.ioc.overview.ioc;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * {@link ApplicationContext}
 * @ClassName IocContainerDemo.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description Ioc 容器示例
 * @Create 2020年06月16日 19:26:00
 */
public class AnnotationApplicationContenxtAsIocContainerDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationApplicationContenxtAsIocContainerDemo.class);
        applicationContext.refresh();

        lookupCollection(applicationContext);
    }

    @Bean
    public User getUser() {
        User user = new User();
        user.setId(2L);
        user.setName("隔壁老王");
        return user;
    }

    private static void lookupCollection(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            Map<String, User> map = listableBeanFactory.getBeansOfType(User.class);
            System.out.println("查找多个user集合：" + map);
        }
    }
}
