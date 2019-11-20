package com.example.demo;

import com.example.demo.pojo.Item;
import com.example.demo.repositry.ItemRepository;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;

/**
 * @Author: Pandy
 * @Date: 2019/3/29 16:39
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    private RestClient restClient;

    @Autowired
    private ItemRepository repository;

    private static final RequestOptions COMMON_OPTIONS;
    static {

        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        //builder.addHeader("Authorization", "Bearer " + TOKEN);
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory
                        .HeapBufferedResponseConsumerFactory(100 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }


    /**
     * 实体类上创建索引的测试
     */
    @Test
    public void testCteate() throws IOException {
        IndexRequest request = new IndexRequest("spring-data", "elasticsearch", "1")
                .source("feature", "high-level-rest-client")
                .setRefreshPolicy(IMMEDIATE);

        CreateIndexRequest request2 = new CreateIndexRequest("test");
        CreateIndexResponse response = elasticsearchClient.indices().create(request2, COMMON_OPTIONS);

        //创建索引库
        //template.createIndex(Item.class);
        //映射关系
        //template.putMapping(Item.class);
        //删除索引
        //template.deleteIndex(Item.class);

    }

    /**
     * template做原生的复杂查询
     * 一般的增删改查
     */
    @Test
    public void indexList() {
        restClient = elasticsearchClient.getLowLevelClient();
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        repository.saveAll(list);
    }

    @Test
    public void testFind(){
        Iterable<Item> all = repository.findAll();
        for (Item item : all) {
            System.out.println("item = " + item);
        }
    }

    @Test
    public void testComplexFind(){
        List<Item> items = repository.findByPriceBetween(2000d, 4000d);
        for (Item item : items) {
            System.out.println("item = " + item);
        }
    }
}
