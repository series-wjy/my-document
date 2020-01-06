package com.example.demo.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @ClassName InvokeLog.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年01月06日 15:01:00
 */
@Data
@Document(indexName = "invoke-index", type = "invoke-type", shards = 1, replicas = 1)
public class InvokeLog {
    @Id
    private String id;
    @Field(type = FieldType.Text, index = true)
    private String type;
    @Field(type = FieldType.Text, index = true)
    private String host;
    @Field(type = FieldType.Text, index = true)
    private String serviceName;
    @Field(type = FieldType.Text, index = true)
    private String url;
    @Field(type = FieldType.Text, index = true)
    private String remote;
    @Field(type = FieldType.Text, index = true)
    private String method;
    @Field(type = FieldType.Text, index = true)
    private String params;
    @Field(type = FieldType.Date,index = true, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private String callTime;
    @Field(type = FieldType.Text, index = true)
    private String duration;

    //查询
    private String startTime;
    private String endTime;
    private String keyword;
}
