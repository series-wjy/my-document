package com.wjy.dependency.lookup;

import com.wjy.dependency.configuration.MyConfiguration;
import com.wjy.dependency.model.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月09日 15:54:00
 */
public class AnnotationApplicationContextDemo {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(ctx.getBean("sysUser"));

        ObjectProvider<User> beanProvider = ctx.getBeanProvider(User.class);
        System.out.println(beanProvider.getObject());
    }

}
