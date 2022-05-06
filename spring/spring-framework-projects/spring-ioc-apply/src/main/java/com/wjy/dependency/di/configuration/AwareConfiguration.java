package com.wjy.dependency.di.configuration;

import com.wjy.dependency.di.aware.AwaredTestBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwareConfiguration {
    
    @Bean
    public AwaredTestBean bbb() {
        return new AwaredTestBean();
    }
}