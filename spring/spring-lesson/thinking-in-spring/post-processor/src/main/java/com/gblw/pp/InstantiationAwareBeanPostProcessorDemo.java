package com.gblw.pp;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月11日 09:19:00
 */
public class InstantiationAwareBeanPostProcessorDemo {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("META-INF/post-processor-beans.xml");
        Car car1 = (Car) applicationContext.getBean("car");
        Car car2 = (Car) applicationContext.getBean("car2");
        System.out.println(car1);
        System.out.println(car2);
        applicationContext.close();
    }
}
