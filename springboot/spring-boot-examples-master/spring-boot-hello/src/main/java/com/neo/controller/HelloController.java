package com.neo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {
	
    @RequestMapping("/")
    public String index() {
        return "Hello Spring Boot 2.0!";
    }


    @RequestMapping(value="/test")
    public String test(HttpServletRequest request) {
        List<Byte[]> temp = new ArrayList<>();
        Byte[] b = new Byte[1024*1024];
        temp.add(b);
        return "success";
    }
}