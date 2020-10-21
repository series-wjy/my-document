package com.wjy.spring.bean.definition;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bean 别名示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年08月12日 22:34:00
 */
public class BeanInstantiationDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:META-INF/bean-instantiation-context.xml");
        User userCreateByStaticMethod = beanFactory.getBean("user-create-by-static-method", User.class);
        User userCreateByFactoryMethod = beanFactory.getBean("user-create-by-factory", User.class);
        User userCreateByFactoryBean = beanFactory.getBean("user-create-by-factory-bean", User.class);

        System.out.println(userCreateByStaticMethod);
        System.out.println(userCreateByFactoryMethod);
        System.out.println(userCreateByFactoryBean);
        System.out.println(userCreateByStaticMethod == userCreateByFactoryMethod);
        System.out.println(userCreateByStaticMethod == userCreateByFactoryBean);
    }
}
