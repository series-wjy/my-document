package com.example.demo.controller;

import com.example.demo.pojo.InvokeLog;
import com.example.demo.pojo.JvmInfo;
import com.example.demo.repositry.InvokeInfoRepository;
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
 * @ClassName InvokeInfoElasticsearchController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description web调用日志查询入口
 * @Create 2019年11月19日 17:17:00
 */
@Controller
@RequestMapping("/invokeinfo")
public class InvokeInfoElasticsearchController extends BaseElasticsearchController {
    @Autowired
    private InvokeInfoRepository repository;

    @PostMapping("/findByCondition")
    public String queryList(JvmInfo item, RedirectAttributes redirectAttributes) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        RangeQueryBuilder timeRange = QueryBuilders.rangeQuery("callTime");
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
            QueryBuilder multiMatch = QueryBuilders.multiMatchQuery(item.getKeyword(), "serviceName", "host", "url");
            qb.should(multiMatch);
        }
        System.out.println(qb.toString(true));
        Iterable<InvokeLog> it = repository.search(qb);

        redirectAttributes.addFlashAttribute("result", Lists.newArrayList(it));
        return "redirect:/invokeresult";
    }
}
