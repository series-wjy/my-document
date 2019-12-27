package com.example.demo.controller;

import com.example.demo.pojo.AppLog;
import com.example.demo.pojo.JvmInfo;
import com.example.demo.repositry.AppLogRepository;
import com.example.demo.repositry.JvmInfoRepository;
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

import java.time.format.DateTimeFormatter;

/**
 * @ClassName JvmInfoElasticsearchController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description jvm日志查询入口
 * @Create 2019年11月19日 17:17:00
 */
@Controller
@RequestMapping("/jvminfo")
public class JvmInfoElasticsearchController extends BaseElasticsearchController {
    @Autowired
    private JvmInfoRepository repository;

    @PostMapping("/findByCondition")
    public String queryList(JvmInfo item, RedirectAttributes redirectAttributes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        RangeQueryBuilder timeRange = QueryBuilders.rangeQuery("createTime");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(Strings.isNotEmpty(item.getStartTime())) {
            timeRange.gt(item.getStartTime() + ",000");
        }
        if(Strings.isNotEmpty(item.getEndTime())) {
            timeRange.lt(item.getEndTime() + ",000");
        }
        qb.must(timeRange);
        if(Strings.isNotEmpty(item.getHostname())) {
            QueryBuilder host = QueryBuilders.termQuery("host", item.getHostname());
            qb.must(host);
        }
        if(Strings.isNotEmpty(item.getKeyword())) {
            QueryBuilder multiMatch = QueryBuilders.multiMatchQuery(item.getKeyword(), "serverName", "hostname", "ip");
            qb.should(multiMatch);
        }
        System.out.println(qb.toString(true));
        Iterable<JvmInfo> it = repository.search(qb);

        redirectAttributes.addFlashAttribute("result", Lists.newArrayList(it));
        return "redirect:/jvmresult";
    }
}
