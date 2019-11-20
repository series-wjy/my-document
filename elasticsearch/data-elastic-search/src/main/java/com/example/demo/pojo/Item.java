package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "test1",type = "item",shards = 1,replicas = 1)
@AllArgsConstructor
public class Item {

    @Field(type = FieldType.Long,index = false)
    @Id
    Long id;

    @Field(type = FieldType.Text,analyzer = "ik_smart",index = true)
    String title; //标题

    @Field(type = FieldType.Text,index = true)
    String category;// 分类

    @Field(type = FieldType.Text,index = true)
    String brand; // 品牌

    @Field(type = FieldType.Double,index = true)
    Double price; // 价格

    @Field(type = FieldType.Keyword,index = false)
    String images; // 图片地址
}