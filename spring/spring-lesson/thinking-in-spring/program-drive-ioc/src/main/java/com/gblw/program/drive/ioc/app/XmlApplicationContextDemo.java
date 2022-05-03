package com.gblw.program.drive.ioc.app;

import com.gblw.program.drive.ioc.entity.Employee;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月14日 10:12:00
 */
public class XmlApplicationContextDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(new ClassPathResource("META-INF/program-drive-ioc.xml"));
        context.refresh();

        Employee employee = (Employee) context.getBean("employee");
        System.out.println(employee);
    }
}
