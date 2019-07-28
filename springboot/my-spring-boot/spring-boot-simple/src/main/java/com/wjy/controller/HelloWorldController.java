package com.wjy.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @RequestMapping("/hello")
    public String index() {
        System.out.println("run method2...");
        System.out.println("run complete3..");
        return "Hello World";
    }
}