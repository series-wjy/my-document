package com.example.demo.repositry;

import com.example.demo.pojo.SvnLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @ClassName AppLogRepository.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 应用日志Repository
 * @Create 2019年11月22日 10:45:00
 */
public interface SvnLogRepository extends ElasticsearchRepository<SvnLog,Long> {
}
