package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName SvnElasticsearchController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description svn日志查询入口
 * @Create 2019年11月19日 17:17:00
 */
@Controller
public class BaseElasticsearchController {
    @GetMapping("/")
    public String index() {
        return "query";
    }

    @GetMapping("/result")
    public String result() {
        return "result";
    }
}
