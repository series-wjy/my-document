package com.example.demo.config;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;

import javax.annotation.Resource;

@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Resource
    RestClientBuilder elasticsearchRestClientBuilder;

    @Bean
    public RestClient restClient() {
        return elasticsearchRestClientBuilder.build();
    }

    /**
     * Elasticsearch客户的配置
     * @Return : org.elasticsearch.client.RestHighLevelClient
     * @Create: 2019/11/19 17:15
     * @Author: wangjiayou
     */
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("192.168.56.101:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
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
