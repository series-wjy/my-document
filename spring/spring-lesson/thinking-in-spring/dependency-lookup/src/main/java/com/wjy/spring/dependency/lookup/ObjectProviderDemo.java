package com.wjy.spring.dependency.lookup;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 通过 {@link ObjectProvider} 进行依赖查找
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月19日 23:00:00
 */
public class ObjectProviderDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(ObjectProviderDemo.class);

        // 启动 spring 容器
        applicationContext.refresh();

        lookupByType(applicationContext);
        lookupIfAvailable(applicationContext);
        lookUpByStreamOps(applicationContext);

        // 关闭 spring 容器
        applicationContext.close();
    }

    private static void lookUpByStreamOps(AnnotationConfigApplicationContext applicationContext) {
        ObjectProvider<String> objectProvider = applicationContext.getBeanProvider(String.class);
        objectProvider.stream().forEach(System.out::println);
    }

    private static void lookupIfAvailable(AnnotationConfigApplicationContext applicationContext) {
        ObjectProvider<User> objectProvider = applicationContext.getBeanProvider(User.class);
        User user = objectProvider.getIfAvailable(User::createUser);
        System.out.println("当前 User 对象：" + user);
    }

    private static void lookupByType(AnnotationConfigApplicationContext applicationContext) {
        ObjectProvider<String> objectProvider = applicationContext.getBeanProvider(String.class);
        String obj = objectProvider.getObject();
        System.out.println(obj);
    }

    @Bean
    @Primary
    public String helloWorld() {
        return "hello, world";
    }

    @Bean
    public String hiJava() {
        return "Hi, JAVA";
    }
}
