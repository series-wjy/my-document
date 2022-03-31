package com.wjy.dependency.injection;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 基于 Annotation 资源的字段依赖注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月30日 21:54:00
 */
public class AnnotationDependencyFieldInjectionDemo {
    @Autowired
    private UserHolder userHolder;
    @Resource
    private UserHolder userHolder2;
    @Autowired
    private static UserHolder userHolder3; // Spring 会忽略掉静态字段

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(AnnotationDependencyFieldInjectionDemo.class);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext);
        // 加载 XML 资源，解析并生成 BeanDefinition
        reader.loadBeanDefinitions("classpath:/META-INF/dependency-lookup-context.xml");
        // 启动 spring 容器
        applicationContext.refresh();

        AnnotationDependencyFieldInjectionDemo demo = applicationContext.getBean(AnnotationDependencyFieldInjectionDemo.class);
        System.out.println(demo);
        System.out.println(demo.userHolder);
        System.out.println(demo.userHolder == demo.userHolder2);
        System.out.println(userHolder3);

        // 按照类型依赖查找
        UserHolder userHolder = applicationContext.getBean(UserHolder.class);
        System.out.println(userHolder);

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
