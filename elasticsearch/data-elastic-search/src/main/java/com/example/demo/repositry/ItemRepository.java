package com.example.demo.repositry;

import com.example.demo.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author: Pandy
 * @Date: 2019/3/29 17:00
 * @Version 1.0
 */
public interface ItemRepository extends ElasticsearchRepository<Item,Long> {
    //范围查询价格
    List<Item> findByPriceBetween(Double begin, Double end);
}
