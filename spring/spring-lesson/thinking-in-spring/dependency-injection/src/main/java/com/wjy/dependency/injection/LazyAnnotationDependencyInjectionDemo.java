package com.wjy.dependency.injection;

import com.wjy.dependency.injection.annotation.UserGroup;
import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Set;

/**
 * 基于 {@link ObjectProvider} 延迟注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月30日 21:54:00
 */
public class LazyAnnotationDependencyInjectionDemo {
    @Autowired
    private User user;

    @Autowired
    private ObjectProvider<User> objectProvider;

    @Autowired
    private ObjectProvider<Set<User>> usersObjectProvider;

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(LazyAnnotationDependencyInjectionDemo.class);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext);
        // 加载 XML 资源，解析并生成 BeanDefinition
        reader.loadBeanDefinitions("classpath:/META-INF/dependency-lookup-context.xml");
        // 启动 spring 容器
        applicationContext.refresh();
        LazyAnnotationDependencyInjectionDemo demo = applicationContext.getBean(LazyAnnotationDependencyInjectionDemo.class);
        // 输出
        System.out.println("demo.user=" + demo.user);
        System.out.println("demo.objectProvider=" + demo.objectProvider.getObject());
        System.out.println("demo.usersObjectProvider=" + demo.usersObjectProvider.getObject());
        demo.objectProvider.forEach(System.out::println);
        // 关闭应用上下文
        applicationContext.close();
    }

    @Bean
    private UserHolder userHolder(User user) {
        UserHolder userHolder = new UserHolder();
        userHolder.setUser(user);
//        return userHolder;
        return new UserHolder(user);
    }
}
