package com.gblw.conditional.v1.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.gblw.conditional.v1.annotation.ConditionalOnClassName;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * jdbc 配置
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 09:08:00
 */
@Configuration
public class JdbcConfiguration {

    @Bean
    @ConditionalOnClassName("com.mysql.jdbc.Driver")
    public DataSource mysql() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("ouyang");
        return dataSource;
    }

    @Bean
    @ConditionalOnClassName("org.h2.Driver")
    public DataSource h2() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:~/test");
        dataSource.setUsername("root");
        dataSource.setPassword("ouyang");
        return dataSource;
    }

    @Bean
    public QueryRunner queryRunner(DataSource dataSource) {
        return new QueryRunner(dataSource);
    }
}
