package com.example.demo.config;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;

import javax.annotation.Resource;

@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Resource
    RestClientBuilder elasticsearchRestClientBuilder;

    @Resource
    RestHighLevelClient elasticsearchRestHighLevelClient;

    /**
     * Elasticsearch RestClient
     * @Return : org.elasticsearch.client.RestClient
     * @Create: 2019/11/22 10:20
     * @Author: wangjiayou
     */
    @Bean
    public RestClient restClient() {
        return elasticsearchRestClientBuilder.build();
    }

    /**
     * Elasticsearch RestHighLevelClient
     * @Return : org.elasticsearch.client.RestHighLevelClient
     * @Create: 2019/11/19 17:15
     * @Author: wangjiayou
     */
    @Override
    public RestHighLevelClient elasticsearchClient() {
        return elasticsearchRestHighLevelClient;
    }

    /**
     * 用户自定义实体映射配置
     * @Return : org.springframework.data.elasticsearch.core.EntityMapper
     * @Create: 2019/11/19 17:14
     * @Author: wangjiayou
     */
    @Bean
    @Override
    public EntityMapper entityMapper() {
        ElasticsearchEntityMapper entityMapper = new ElasticsearchEntityMapper(
                elasticsearchMappingContext(), new DefaultConversionService()
        );
        entityMapper.setConversions(elasticsearchCustomConversions());
        return entityMapper;
    }
}
