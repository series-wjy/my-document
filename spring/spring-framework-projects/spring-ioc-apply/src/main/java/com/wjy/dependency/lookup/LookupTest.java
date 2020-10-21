package com.wjy.dependency.lookup;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月08日 17:45:00
 */
public class LookupTest {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("basic_dl/quickstart-byname.xml");
        System.out.println(beanFactory.getBean("user"));
    }
}
