package com.gblw.conditional.v2.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.gblw.conditional.v2.annotation.ConditionalOnClassName;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * h2 jdbc 配置
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 09:08:00
 */
@Configuration
public class H2JdbcConfiguration extends JdbcConfigurationAdapter {

    @Bean
    @ConditionalOnClassName("org.h2.Driver")
    public DataSource h2() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(environment.getProperty("jdbc.url"));
        dataSource.setUsername(environment.getProperty("jdbc.username"));
        dataSource.setPassword(environment.getProperty("jdbc.password"));
        return dataSource;
    }
}
