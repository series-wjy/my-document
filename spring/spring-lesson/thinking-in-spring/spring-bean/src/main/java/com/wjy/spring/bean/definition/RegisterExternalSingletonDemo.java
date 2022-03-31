package com.wjy.spring.bean.definition;

import com.wjy.spring.bean.factory.DefaultUserFactory;
import com.wjy.spring.bean.factory.UserFactory;
import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 注入外部单例 bean 示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月19日 23:10:00
 */
public class RegisterExternalSingletonDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(AnnotationBeanDefinitionDemo.class);

        // 实例化 bean
        UserFactory userFactory = new DefaultUserFactory();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("userFactory", userFactory);
        UserFactory userFactoryByLookUp = (UserFactory) beanFactory.getBean("userFactory");
        System.out.println("userFactory == userFactoryByLookUp? : " + (userFactory == userFactoryByLookUp));

        // 启动 spring 容器
        applicationContext.refresh();

        // 关闭应用上下文
        applicationContext.close();
    }
}
