package com.wjy.spring.bean.definition;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bean 别名示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年08月12日 22:34:00
 */
public class BeanAliasDemo {
    public static void main(String[] args) {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:/META-INF/bean-definitions-context.xml");
        User user = beanFactory.getBean("user", User.class);
        User gebilaowang = beanFactory.getBean("gebilaowang", User.class);
        System.out.println("user 是否与 gebilaowang 相同？" + (user == gebilaowang));
    }
}
