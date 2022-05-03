package com.gblw.program.drive.ioc.app;

import com.gblw.program.drive.ioc.entity.Boss;
import com.gblw.program.drive.ioc.entity.Employee;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月14日 08:58:00
 */
public class DriveIocApplicationContextDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        BeanDefinition employeeBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Employee.class)
                .addPropertyValue("name", "暴走王尼玛")
                .getBeanDefinition();
        applicationContext.registerBeanDefinition("employee", employeeBeanDefinition);

        BeanDefinition bossBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Boss.class)
                .addPropertyValue("name", "隔壁老王")
                .addPropertyReference("employee", "employee")
                .getBeanDefinition();
        applicationContext.registerBeanDefinition("boss", bossBeanDefinition);
        applicationContext.refresh();
        System.out.println("ApplicationContext has been refreshed......");


        Boss boss = (Boss) applicationContext.getBean("boss");
        System.out.println(boss);
    }
}
