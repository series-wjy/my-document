package com.gblw.conditional.v3.configuration;

import com.gblw.conditional.v3.registry.DataSourceRegisterPostProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 15:25:00
 */
@Configuration
public class DataSourceConfiguration {

    @Bean
    public DataSourceRegisterPostProcessor postProcessor() {
        return new DataSourceRegisterPostProcessor();
    }

    @Bean
    public QueryRunner queryRunner(DataSource dataSource) {
        return new QueryRunner(dataSource);
    }
}
