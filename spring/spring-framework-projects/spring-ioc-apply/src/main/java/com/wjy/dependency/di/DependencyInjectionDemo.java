package com.wjy.dependency.lookup;

import com.wjy.dependency.model.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月09日 15:54:00
 */
public class ApplicationContextDemo {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("basic_dl/quickstart-byname.xml");
        System.out.println(ctx.getBean("user"));

        ObjectProvider<User> beanProvider = ctx.getBeanProvider(User.class);

        beanProvider.ifAvailable(PrintObject :: print);
    }

    static class PrintObject {

        public static void print(Object o) {
            System.out.println(o);
        }

        public void print2() {

        }
    }
}
