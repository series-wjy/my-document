package com.example.demo.controller;

import com.example.demo.pojo.Item;
import com.example.demo.repositry.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SvnElasticsearchController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description svn日志查询入口
 * @Create 2019年11月19日 17:17:00
 */
@Controller
public class SvnElasticsearchController {

    @Autowired
    private ItemRepository repository;

    @GetMapping("/")
    public String index() {
        return "query";
    }

    @GetMapping("/result")
    public String result() {
        return "result";
    }

    @GetMapping("/findByCondition")
    public List<Item> queryList(@RequestBody Item item) {
        List<Item> list = repository.findByPriceBetween(2000d, 4000d);
        return list;
    }
}
