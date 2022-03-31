package com.wjy.dependency.injection;

import com.wjy.dependency.injection.annotation.UserGroup;
import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.xml.transform.Source;
import java.util.Collection;

/**
 * 基于 {@link Qualifier} 分组注入
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月30日 21:54:00
 */
public class AnnotationQualifierDependencyInjectionDemo {
    @Autowired
    private User superUser;

    @Autowired
    @Qualifier("user")
    private User user;

    /**
     * 只会输出两个 bean，具体是因为自引用的问题，参考下面的 issues
     * https://github.com/spring-projects/spring-framework/issues/23934
     */
    @Autowired
    private Collection<User> allUsers;

    @Autowired
    @Qualifier
    private Collection<User> qualifierUsers;

    @Autowired
    @UserGroup
    private Collection<User> groupedUsers;

    @Bean
    @Qualifier
    private User user1() {
        return createUser(7);
    }

    @Bean
    @Qualifier
    private User user2() {
        return createUser(8);
    }

    @Bean
    @UserGroup
    private User user3() {
        return createUser(9);
    }

    @Bean
    @UserGroup
    private User user4() {
        return createUser(10);
    }


    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public static void main(String[] args) {
        // 创建 BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 把当前类作为配置类注入容器
        applicationContext.register(AnnotationQualifierDependencyInjectionDemo.class);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext);
        // 加载 XML 资源，解析并生成 BeanDefinition
        reader.loadBeanDefinitions("classpath:/META-INF/dependency-lookup-context.xml");
        // 启动 spring 容器
        applicationContext.refresh();
        AnnotationQualifierDependencyInjectionDemo demo = applicationContext.getBean(AnnotationQualifierDependencyInjectionDemo.class);
        // 输出
        System.out.println("demo.superUser=" + demo.superUser);
        System.out.println("demo.user=" + demo.user);
        System.out.println("demo.allUsers=" + demo.allUsers);
        System.out.println("demo.qualifierUsers=" + demo.qualifierUsers);
        System.out.println("demo.groupedUsers=" + demo.groupedUsers);

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
