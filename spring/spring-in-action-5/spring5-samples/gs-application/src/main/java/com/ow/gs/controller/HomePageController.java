package com.ow.gs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName HomePageController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月09日 16:21:00
 */
@Controller
public class HomePageController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
