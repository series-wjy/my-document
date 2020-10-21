package com.wjy.dependency.configuration;

import com.wjy.dependency.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月09日 16:26:00
 */
@Configuration
@ComponentScan("com.wjy.dependency")
@ImportResource("classpath:basic_dl/quickstart-byname.xml")
public class MyConfiguration {

    @Bean
    public User sysUser() {
        return new User();
    }
}
