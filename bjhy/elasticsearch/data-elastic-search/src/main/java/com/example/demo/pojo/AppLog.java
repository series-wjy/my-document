package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName AppLog.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 应用日志
 * @Create 2019年11月22日 10:45:00
 */
@Data
@Document(indexName = "app-index",type = "app-type",shards = 1,replicas = 1)
public class AppLog {

    @Id
    private String id;
    @Field(type = FieldType.Text,index = true)
    private String host;
    @Field(type = FieldType.Date,index = true, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss,SSS")
    private LocalDateTime dateTime;
    @Field(type = FieldType.Text,index = true)
    private String logLevel;
    @Field(type = FieldType.Text,index = true)
    private String threadPool;
    @Field(type = FieldType.Text,index = true)
    private String execClass;
    @Field(type = FieldType.Text,index = true)
    private String logContent;

    //查询
    private String startTime;
    private String endTime;
    private String keyword;

    public AppLog(String id, String host, LocalDateTime dateTime, String logLevel,
                  String threadPool, String execClass, String logContent) {
        this.id = id;
        this.host = host;
        this.dateTime = dateTime;
        this.logLevel = logLevel;
        this.threadPool = threadPool;
        this.execClass = execClass;
        this.logContent = logContent;
    }
}
