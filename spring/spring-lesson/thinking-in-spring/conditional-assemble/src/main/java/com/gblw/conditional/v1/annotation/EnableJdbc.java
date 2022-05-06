package com.gblw.conditional.v1.annotation;

import com.gblw.conditional.spi.OracleDataSource;
import com.gblw.conditional.v1.configuration.JdbcConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启 jdbc
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月15日 09:15:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({JdbcConfiguration.class, OracleDataSource.class})
public @interface EnableJdbc {
}
