package com.wjy.provider.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        System.out.println(name + "comming here......");
        return "hello "+name+"，this is first messge";
    }

    @RequestMapping("/foo")
    public String foo(@RequestParam String foo) {
        return "hello "+foo+"!";
    }
}