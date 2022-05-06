package com.gblw.conditional.v1.app;

import com.alibaba.druid.pool.DruidDataSource;
import com.gblw.conditional.spi.OracleDataSource;
import com.gblw.conditional.v1.annotation.EnableJdbc;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * 测试条件装配
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 09:58:00
 */
@Configuration
@EnableJdbc
public class EnableJdbcApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EnableJdbcApplication.class);
//        context.scan("com.gblw.cond");
//        context.refresh();
        DruidDataSource bean = context.getBean(DruidDataSource .class);
        System.out.println(bean.getUrl());

        OracleDataSource oracle = context.getBean(OracleDataSource.class);
        System.out.println(oracle);
    }
}
