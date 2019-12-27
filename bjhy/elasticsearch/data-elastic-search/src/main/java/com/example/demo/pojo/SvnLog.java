package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @ClassName SvnLog.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年11月22日 10:45:00
 */
@Data
@Document(indexName = "svn-index",type = "svn-type",shards = 1,replicas = 1)
@AllArgsConstructor
public class SvnLog {
    @Id
    private String id;

    @Field(type = FieldType.Date,index = true)
    private Date date;
    @Field(type = FieldType.Date,index = true)
    private Date time;
    @Field(type = FieldType.Text,index = true)
    private String user;
    @Field(type = FieldType.Integer,index = true)
    private int add;
    @Field(type = FieldType.Integer,index = true)
    private int modified;
    @Field(type = FieldType.Integer,index = true)
    private int delete;
    @Field(type = FieldType.Nested,index = true)
    private List<String> files;
}
