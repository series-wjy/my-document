package com.gblw.conditional.v2.annotation;

import com.gblw.conditional.v2.configuration.MysqlJdbcConfiguration;
import com.gblw.conditional.v2.selector.JdbcConfigurationSelector;
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
@Import(JdbcConfigurationSelector.class)
public @interface EnableJdbc {
}
