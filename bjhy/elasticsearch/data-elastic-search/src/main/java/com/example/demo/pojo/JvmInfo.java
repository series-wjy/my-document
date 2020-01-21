package com.example.demo.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(indexName = "jvm-index", type = "jvm-type", shards = 1, replicas = 1)
public class JvmInfo {
    @Id
    private String id;
    @Field(type = FieldType.Text, index = true)
    private String serverInstanceId;
    @Field(type = FieldType.Text, index = true)
    private String serverName;
    @Field(type = FieldType.Text, index = true)
    private String hostname;
    @Field(type = FieldType.Text, index = true)
    private String host;
    @Field(type = FieldType.Date,index = true, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private LocalDateTime collectTime;

    @Field(type = FieldType.Integer, index = true)
    private int threadCount;
    @Field(type = FieldType.Long, index = true)
    private long threadCpuTime;
    @Field(type = FieldType.Long, index = true)
    private long threadUserTime;

    @Field(type = FieldType.Long, index = true)
    private long loadedClassCount;
    @Field(type = FieldType.Long, index = true)
    private long totalLoadedClassCount;
    @Field(type = FieldType.Long, index = true)
    private long unloadedClassCount;
    @Field(type = FieldType.Integer, index = true)
    private int availableProcessors;
    @Field(type = FieldType.Double, index = true)
    private double systemLoadAverage;

    @Field(type = FieldType.Object, index = true)
    private Map<String, MemoryUsage> heapMemoryInfo = new HashMap<>();
    @Field(type = FieldType.Object, index = true)
    private MemoryUsage heapMemoryUsage;
    @Field(type = FieldType.Object, index = true)
    private MemoryUsage nonHeapMemoryUsage;

    //查询
    private String startTime;
    private String endTime;
    private String keyword;
}