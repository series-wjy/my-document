package com.wjy.jpa.controller;

import com.wjy.jpa.model.OptimisticLock;
import com.wjy.jpa.service.OptimisticLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月24日 11:21:00
 */
@RestController
@RequestMapping("api/optimistic-lock")
public class OptimisticLockController {
    @Autowired
    private OptimisticLockService optimisticLockService;


    @PostMapping(path = "update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OptimisticLock optimisticLock(@RequestBody OptimisticLock entity) {
        return optimisticLockService.updateByOptimisticLock(entity);
    }
}
