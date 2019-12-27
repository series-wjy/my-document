package com.example.demo.controller;

import com.example.demo.pojo.AppLog;
import com.example.demo.repositry.AppLogRepository;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName AppLogElasticsearchController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description app日志查询入口
 * @Create 2019年11月19日 17:17:00
 */
@Controller
@RequestMapping("/applog")
public class AppLogElasticsearchController extends BaseElasticsearchController {
    @Autowired
    private AppLogRepository repository;

    @PostMapping("/findByCondition")
    public String queryList(AppLog item,
                            RedirectAttributes redirectAttributes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        RangeQueryBuilder timeRange = QueryBuilders.rangeQuery("dateTime");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(Strings.isNotEmpty(item.getStartTime())) {
            timeRange.gt(item.getStartTime() + ",000");
        }
        if(Strings.isNotEmpty(item.getEndTime())) {
            timeRange.lt(item.getEndTime() + ",000");
        }
        qb.must(timeRange);
        if(Strings.isNotEmpty(item.getHost())) {
            QueryBuilder host = QueryBuilders.termQuery("host", item.getHost());
            qb.must(host);
        }
        if(Strings.isNotEmpty(item.getLogLevel())) {
            QueryBuilder logLevel = QueryBuilders.termQuery("logLevel", item.getLogLevel());
            qb.must(logLevel);
        }
        if(Strings.isNotEmpty(item.getKeyword())) {
            QueryBuilder multiMatch = QueryBuilders.multiMatchQuery(item.getKeyword(), "logContent", "threadPool", "execClass");
            qb.should(multiMatch);
        }
        System.out.println(qb.toString(true));
        Iterable<AppLog> it = repository.search(qb);

        redirectAttributes.addFlashAttribute("result", Lists.newArrayList(it));
        return "redirect:/appresult";
    }
}
