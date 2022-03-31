package com.wjy.dependency.injection;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * 基于 XML 资源的 setter 依赖注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月30日 21:54:00
 */
public class XmlDependencySetterInjectionDemo {
    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // 加载 XML 资源，解析并生成 BeanDefinition
        reader.loadBeanDefinitions("classpath:/META-INF/dependency-setter-injection.xml");

        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println(userHolder);
    }
}
