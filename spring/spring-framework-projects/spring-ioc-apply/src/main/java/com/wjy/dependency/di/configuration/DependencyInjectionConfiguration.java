package com.wjy.dependency.di.configuration;

import com.wjy.dependency.configuration.MyConfiguration;
import com.wjy.dependency.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月11日 16:53:00
 */
@Configuration
@ComponentScan(basePackages = "com.wjy.dependency",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
                pattern = "com.wjy.dependency.configuration.*Configuration"))
public class DependencyInjectionConfiguration {

    @Bean
    public User superUser() {
        User user = new User();
        user.setName("superUser");
        return user;
    }
}
