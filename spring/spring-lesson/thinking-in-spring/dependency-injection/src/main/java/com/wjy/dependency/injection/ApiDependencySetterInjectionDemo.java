package com.wjy.dependency.injection;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 基于 API 资源的 setter 依赖注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月30日 21:54:00
 */
public class ApiDependencySetterInjectionDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 生成 UserHolder 的 BeanDefinition
        BeanDefinition userHolderBeanDefinition = createUserHolderBeanDefinition();

        // 注册 UserHolder 的 BeanDefinition
        applicationContext.registerBeanDefinition("userHolder", userHolderBeanDefinition);

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext);
        // 加载 XML 资源，解析并生成 BeanDefinition
        reader.loadBeanDefinitions("classpath:/META-INF/dependency-lookup-context.xml");
        // 启动 spring 容器
        applicationContext.refresh();
        // 按照类型依赖查找
        UserHolder userHolder = applicationContext.getBean(UserHolder.class);
        System.out.println(userHolder);

        // 关闭应用上下文
        applicationContext.close();
    }

    /**
     * 创建 UserHolder 的 BeanDefinition
     * @return
     */
    private static BeanDefinition createUserHolderBeanDefinition() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(UserHolder.class);
        builder.addPropertyReference("user", "superUser");
        return builder.getBeanDefinition();
    }
}
