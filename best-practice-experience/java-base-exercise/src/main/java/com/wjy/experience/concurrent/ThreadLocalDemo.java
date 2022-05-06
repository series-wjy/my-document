package com.wjy.experience.concurrent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程池复用导致获取信息错乱（ThreadLocal）
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月21日 17:39:00
 */
@RestController
@RequestMapping("data-cache")
public class ThreadLocalDemo {

    private static final ThreadLocal<Integer> currentUser = ThreadLocal.withInitial(() -> null);


    /**
     * 有缺陷的 ThreadLocal 信息获取
     * @param userId
     * @return
     */
    @GetMapping("defective")
    public Map wrong(@RequestParam("userId") Integer userId) {
        //设置用户信息之前先查询一次ThreadLocal中的用户信息
        String before  = Thread.currentThread().getName() + ":" + currentUser.get();
        //设置用户信息到ThreadLocal
        currentUser.set(userId);
        //设置用户信息之后再查询一次ThreadLocal中的用户信息
        String after  = Thread.currentThread().getName() + ":" + currentUser.get();
        //汇总输出两次查询结果
        Map result = new HashMap();
        result.put("before", before);
        result.put("after", after);
        return result;
    }

    /**
     * 使用完 ThreadLocal 后，在 finally 中清空缓存
     * @param userId
     * @return
     */
    @GetMapping("perfect")
    public Map right(@RequestParam("userId") Integer userId) {
        String before  = Thread.currentThread().getName() + ":" + currentUser.get();
        currentUser.set(userId);
        try {
            String after = Thread.currentThread().getName() + ":" + currentUser.get();
            Map result = new HashMap();
            result.put("before", before);
            result.put("after", after);
            return result;
        } finally {
            //在finally代码块中删除ThreadLocal中的数据，确保数据不串
            currentUser.remove();
        }
    }

}
