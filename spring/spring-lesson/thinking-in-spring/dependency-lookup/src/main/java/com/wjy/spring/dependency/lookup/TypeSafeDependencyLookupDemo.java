package com.wjy.spring.dependency.lookup;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 类型安全的依赖查找
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月27日 23:20:00
 */
public class TypeSafeDependencyLookupDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(TypeSafeDependencyLookupDemo.class);

        // 启动 spring 容器
        applicationContext.refresh();

        displayBeanFactoryGetBean(applicationContext);
        displayObjectFactoryGetObject(applicationContext);
        displayObjectProviderIfAvailable(applicationContext);
        displayListableBeanFactoryGetBeansByType(applicationContext);
        displayObjectProviderStreamOps(applicationContext);


        applicationContext.close();
    }

    private static void displayObjectProviderStreamOps(AnnotationConfigApplicationContext applicationContext) {
        ObjectProvider<User> objectProvider = applicationContext.getBeanProvider(User.class);
        printBeansException("displayObjectProviderIfAvailable", () -> objectProvider.forEach(System.out :: println));
    }

    private static void displayListableBeanFactoryGetBeansByType(ListableBeanFactory applicationContext) {
        printBeansException("displayListableBeanFactoryGetBeansByType", () -> applicationContext.getBeansOfType(User.class));
    }

    private static void displayObjectProviderIfAvailable(AnnotationConfigApplicationContext applicationContext) {
        ObjectProvider<User> objectProvider = applicationContext.getBeanProvider(User.class);
        printBeansException("displayObjectProviderIfAvailable", () -> objectProvider.getIfAvailable());
    }

    private static void displayObjectFactoryGetObject(AnnotationConfigApplicationContext applicationContext) {
        ObjectFactory<User> objectProvider = applicationContext.getBeanProvider(User.class);
        printBeansException("displayObjectFactoryGetObject", () -> objectProvider.getObject());
    }

    private static void displayBeanFactoryGetBean(BeanFactory beanFactory) {
        printBeansException("displayBeanFactoryGetBean", () -> beanFactory.getBean(User.class));
    }

    private static void printBeansException(String source, Runnable runnable) {
        System.err.println("=======================================");
        System.err.println("source from : " + source);
        try {
            runnable.run();
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }
}
