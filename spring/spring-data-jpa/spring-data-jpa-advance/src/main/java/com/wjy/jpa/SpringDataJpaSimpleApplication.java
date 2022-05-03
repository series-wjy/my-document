package com.wjy.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry // 开启重试
public class SpringDataJpaSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaSimpleApplication.class, args);
    }

}
