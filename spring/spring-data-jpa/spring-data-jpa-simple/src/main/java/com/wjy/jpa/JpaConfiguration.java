package com.wjy.jpa;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;

/**
 * 配置 JPA 相关信息
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月05日 14:17:00
 */
@EnableJpaRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND)
public class JpaConfiguration {
}
