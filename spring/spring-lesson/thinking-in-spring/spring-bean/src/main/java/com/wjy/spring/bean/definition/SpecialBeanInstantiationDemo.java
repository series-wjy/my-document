package com.wjy.spring.bean.definition;

import com.wjy.spring.bean.factory.DefaultUserFactory;
import com.wjy.spring.bean.factory.UserFactory;
import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.serviceloader.ServiceLoaderFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Iterator;
import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;

/**
 * 特殊方式实例化 Bean
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年08月12日 22:34:00
 */
public class SpecialBeanInstantiationDemo {
    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/special-bean-instantiation-context.xml");
        ServiceLoader<UserFactory> serviceLoader = ctx.getBean("user-create-by-service-loader", ServiceLoader.class);
        displayServiceLoader(serviceLoader);
        demoServiceLoader();

        AutowireCapableBeanFactory autowireCapableBeanFactory = ctx.getAutowireCapableBeanFactory();
        DefaultUserFactory defaultUserFactory = autowireCapableBeanFactory.createBean(DefaultUserFactory.class);
        System.out.println(defaultUserFactory.createUser());
        System.out.println(autowireCapableBeanFactory.createBean(User.class));

    }

    public static void demoServiceLoader() {
        ServiceLoader<UserFactory> serviceLoader = load(UserFactory.class, Thread.currentThread().getContextClassLoader());
        displayServiceLoader(serviceLoader);
    }

    public static void displayServiceLoader(ServiceLoader<UserFactory> serviceLoader) {
        Iterator<UserFactory> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().createUser());
        }
    }
}
