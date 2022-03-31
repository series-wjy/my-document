package com.wjy.spring.dependency.lookup;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 层次性依赖查找
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月26日 23:14:00
 */
public class HierarchicalDependencyLookupDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(ObjectProviderDemo.class);

        // 获取层次性 bean 依赖查找容器（HierarchicalBeanFactory<-ConfigurableBeanFactory<-ConfigurableListableBeanFactory
        ConfigurableListableBeanFactory listableBeanFactory = applicationContext.getBeanFactory();
        System.out.println("当前 BeanFactory 的 parent BeanFactory：" + listableBeanFactory.getParentBeanFactory());

        // 设置 parent BeanFactory
        HierarchicalBeanFactory parentBeanFactory = createParentBeanFactory();
        listableBeanFactory.setParentBeanFactory(parentBeanFactory);
        System.out.println("当前 BeanFactory 的 parent BeanFactory：" + listableBeanFactory.getParentBeanFactory());

        displayContainsBean(listableBeanFactory, "user");
        displayContainsBean(parentBeanFactory, "user");
        displayContainsLocalBean(listableBeanFactory, "user");
        displayContainsLocalBean(parentBeanFactory, "user");


        // 启动 spring 容器
        applicationContext.refresh();

        // 关闭 spring 容器
        applicationContext.close();
    }

    private static void displayContainsBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("当前 BeanFactory [%s] 是否包含 bean[name : %s] : %s\n", beanFactory, beanName,
                containsBean(beanFactory, beanName));
    }

    private static boolean containsBean(HierarchicalBeanFactory beanFactory, String beanName) {
        BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
        if(parentBeanFactory instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory parentHierarchicalBeanFactory = HierarchicalBeanFactory.class.cast(parentBeanFactory);
            if(containsBean(parentHierarchicalBeanFactory, beanName)) {
                return true;
            }
        }
        return beanFactory.containsLocalBean(beanName);
    }

    private static void displayContainsLocalBean(HierarchicalBeanFactory beanFactory, String beanName) {
        System.out.printf("当前 BeanFactory[%s] 是否包含 local bean[name:%s]:%s\n", beanFactory, beanName,
                beanFactory.containsLocalBean(beanName));

    }

    private static HierarchicalBeanFactory createParentBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        String location = "classpath:\\META-INF\\dependency-lookup-context.xml";
        reader.loadBeanDefinitions(location);
        return beanFactory;
    }
}
