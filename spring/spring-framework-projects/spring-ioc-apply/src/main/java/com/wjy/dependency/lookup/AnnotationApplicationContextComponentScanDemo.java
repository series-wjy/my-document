package com.wjy.dependency.lookup;

import com.wjy.dependency.configuration.MyConfiguration;
import com.wjy.dependency.model.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.stream.Stream;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月09日 15:54:00
 */
public class AnnotationApplicationContextComponentScanDemo {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MyConfiguration.class);
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        Stream.of(beanDefinitionNames).forEach(System.out :: println);

    }

}
