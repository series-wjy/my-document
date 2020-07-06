package com.wjy.tis.ioc.overview.dependency.inject;

import com.wjy.tis.ioc.overview.repository.UserRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName InjectionDemo.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 依赖注入
 * @Create 2020年06月05日 22:32:00
 */
public class DependencyInjectionDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:\\META-INF\\dependency-injection-context.xml");

        collectionPropertyInjection(beanFactory);

        UserRepository userRepository = (UserRepository) beanFactory.getBean("userRepository");
        System.out.println(userRepository.getBeanFactory());
        System.out.println(userRepository.getBeanFactory() == beanFactory);

        ObjectFactory userObjectFactory = userRepository.getUserObjectFactory();
        System.out.println(userObjectFactory.getObject());


        ObjectFactory applicationContextObjectFactory = userRepository.getApplicationContextObjectFactory();
        System.out.println(applicationContextObjectFactory.getObject());
        System.out.println(applicationContextObjectFactory.getObject() == beanFactory);
    }

    private static void collectionPropertyInjection(BeanFactory beanFactory) {
        UserRepository userRepository = (UserRepository) beanFactory.getBean("userRepository");
        System.out.println(userRepository.getList());
    }
}
