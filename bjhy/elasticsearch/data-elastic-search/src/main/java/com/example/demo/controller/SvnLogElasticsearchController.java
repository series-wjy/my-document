package com.example.demo.controller;

import com.example.demo.repositry.SvnLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * @ClassName SvnElasticsearchController.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description svn日志查询入口
 * @Create 2019年11月19日 17:17:00
 */
@Controller
@RequestMapping("/svnlog")
public class SvnLogElasticsearchController {

    @Autowired
    private SvnLogRepository repository;

    @PostMapping("/findByCondition")
    public String queryList(@RequestParam String keyword,
                            RedirectAttributes redirectAttributes) {
        return "redirect:/result";
    }
}
