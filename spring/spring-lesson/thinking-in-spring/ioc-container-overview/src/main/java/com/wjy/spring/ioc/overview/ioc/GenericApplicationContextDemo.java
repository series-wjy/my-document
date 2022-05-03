package com.wjy.spring.ioc.overview.ioc;

import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年03月31日 13:21:00
 */
public class GenericApplicationContextDemo {
    public static void main(String[] args) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("META-INF/dependency-injection-context.xml");
        context.getEnvironment().setActiveProfiles("city");
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for(String beanName : beanDefinitionNames) {
            System.out.println(beanName);
        }
    }
}
