package com.gblw.program.drive.ioc.app;

import com.gblw.program.drive.ioc.entity.Boss;
import com.gblw.program.drive.ioc.entity.Employee;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.beans.Introspector;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月14日 09:27:00
 */
public class ScannerApplicationContextDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);

        BeanDefinition employeeBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Employee.class)
                .addPropertyValue("name", "暴走王尼玛")
                .getBeanDefinition();
        context.registerBeanDefinition("employee", employeeBeanDefinition);

        scanner.scan("com.gblw.program.drive.ioc.entity");
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            return metadataReader.getClassMetadata().getClassName().equals(Boss.class.getName());
        });
        scanner.findCandidateComponents("com.gblw.program.drive.ioc.entity")
                .forEach(beanDefinition -> {
                    String beanName = beanDefinition.getBeanClassName();
                    MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
                    propertyValues.addPropertyValue("name", beanName);

                    propertyValues.addPropertyValue("employee", new RuntimeBeanReference("employee"));
                    context.registerBeanDefinition(Introspector.decapitalize(beanName.substring(beanName.lastIndexOf(".") + 1)), beanDefinition);
                });
        context.refresh();

        Boss boss = (Boss) context.getBean("boss");
        System.out.println(boss);

    }
}
