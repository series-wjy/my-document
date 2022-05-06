package com.gblw.conditional.v2.configuration;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 11:45:00
 */
public abstract class JdbcConfigurationAdapter implements EnvironmentAware {
    protected Environment environment;

    @Bean
    public QueryRunner queryRunner(DataSource dataSource) {
        return new QueryRunner(dataSource);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
