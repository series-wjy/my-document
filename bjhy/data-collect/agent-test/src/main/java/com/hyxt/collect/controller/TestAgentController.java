package com.hyxt.collect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName TestAgentController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年01月06日 13:46:00
 */
@RequestMapping("/")
@Controller
public class TestAgentController {

    @RequestMapping("/test")
    @ResponseBody
    public String testRequest() {
        System.out.println("========================执行controller请求===================");
        return "sucess";
    }
}
