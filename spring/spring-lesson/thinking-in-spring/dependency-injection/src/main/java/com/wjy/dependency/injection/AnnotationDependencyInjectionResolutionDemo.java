package com.wjy.dependency.injection;

import com.wjy.dependency.injection.annotation.MyAutowired;
import com.wjy.dependency.injection.annotation.MyInject;
import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.context.annotation.AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME;

/**
 * 基于 Annotation 资源的 constructor 依赖注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月30日 21:54:00
 */
@Configuration
public class AnnotationDependencyInjectionResolutionDemo {
    @Autowired
    @Lazy
    private User lazyUser;

    @Autowired
    private User user;
    @Autowired
    private Map<String, User> users;

    @Inject
    private User injectUser;

    @MyAutowired
    private User myUser;

    @MyInject
    private User myInjectUser;

//    @Bean(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)
//    public static AutowiredAnnotationBeanPostProcessor beanPostProcessor() {
//        AutowiredAnnotationBeanPostProcessor beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
//        Set typeSet = new LinkedHashSet();
//        typeSet.addAll(Arrays.asList(Autowired.class, Inject.class, MyInject.class));
//        beanPostProcessor.setAutowiredAnnotationTypes(typeSet);
//        return beanPostProcessor;
//    }

    @Bean
    public static AutowiredAnnotationBeanPostProcessor beanPostProcessor() {
        AutowiredAnnotationBeanPostProcessor beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        beanPostProcessor.setAutowiredAnnotationType(MyInject.class);
        beanPostProcessor.setOrder(Ordered.LOWEST_PRECEDENCE - 3);
        return beanPostProcessor;
    }

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(AnnotationDependencyInjectionResolutionDemo.class);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext);
        // 加载 XML 资源，解析并生成 BeanDefinition
        reader.loadBeanDefinitions("classpath:/META-INF/dependency-lookup-context.xml");
        // 启动 spring 容器
        applicationContext.refresh();
        AnnotationDependencyInjectionResolutionDemo demo = applicationContext.getBean(AnnotationDependencyInjectionResolutionDemo.class);
        // 按照类型依赖查找
        System.out.println(demo.user);
        System.out.println(demo.users);
        System.out.println(demo.lazyUser);
        System.out.println(demo.injectUser);
        System.out.println(demo.myUser);
        System.out.println(demo.myInjectUser);

        // 关闭应用上下文
        applicationContext.close();
    }
}
