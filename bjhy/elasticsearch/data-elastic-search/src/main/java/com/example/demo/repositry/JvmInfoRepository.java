package com.example.demo.repositry;

import com.example.demo.pojo.AppLog;
import com.example.demo.pojo.JvmInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @ClassName JvmInfoRepository.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 应用日志Repository
 * @Create 2019年11月22日 10:45:00
 */
public interface JvmInfoRepository extends ElasticsearchRepository<JvmInfo,Long> {

}
