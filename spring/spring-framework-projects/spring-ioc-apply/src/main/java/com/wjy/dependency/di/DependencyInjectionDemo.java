package com.wjy.dependency.di;

import com.wjy.dependency.di.configuration.DependencyInjectionConfiguration;
import com.wjy.dependency.model.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.stream.Stream;

/**
 * 依赖注入 Demo
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月09日 15:54:00
 */
public class DependencyInjectionDemo {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(DependencyInjectionConfiguration.class);
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        Stream.of(beanDefinitionNames).forEach(System.out :: println);

        String[] beanNamesForType = ctx.getBeanNamesForType(User.class);
        Stream.of(beanNamesForType).forEach(System.out :: println);
        System.out.println(ctx.getBean("superUser"));
        //System.out.println(ctx.getBean("user"));
        System.out.println(ctx.getBean("account"));

        // @Resource 注入
        System.out.println(ctx.getBean("department"));
    }
}
