package com.wjy.spring.bean.definition;

import com.wjy.spring.ioc.overview.domain.User;
import com.wjy.spring.ioc.overview.ioc.AnnotationApplicationContenxtAsIocContainerDemo;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 注解 BeanDefinition 示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月12日 16:53:00
 */
// 3、通过 @Import 方式注入
@Import(AnnotationBeanDefinitionDemo.Config.class)
public class AnnotationBeanDefinitionDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(AnnotationBeanDefinitionDemo.class);

        // 通过 BeanDefiniton 注册 API 注册 bean
        registerBeanDefinition(applicationContext, "gbxw", User.class);
        registerBeanDefinition(applicationContext, "", User.class);

        // 启动 spring 容器
        applicationContext.refresh();
        // 按照类型依赖查找
        System.out.println("Config 类型的所有 beans" + applicationContext.getBeansOfType(Config.class));
        System.out.println("User 类型的所有 beans" + applicationContext.getBeansOfType(User.class));

        // 关闭应用上下文
        applicationContext.close();
    }

    public static void registerBeanDefinition(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        beanDefinitionBuilder
                .addPropertyValue("id", 3)
                .addPropertyValue("name", "隔壁小王");

        // 判断 beanName 是否存在
        if (StringUtils.hasText(beanName)) {
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        } else {
            // 非命名 bean 注册
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinitionBuilder.getBeanDefinition(), registry);
        }
    }

    // 2、通过 @Component 注入
    @Component
    public static class Config {

        // 1、通过 @Bean 方式定义
        @Bean(name = "gblw")
        public User user() {
            User user = new User();
            user.setId(2L);
            user.setName("隔壁老王");
            return user;
        }
    }

}
