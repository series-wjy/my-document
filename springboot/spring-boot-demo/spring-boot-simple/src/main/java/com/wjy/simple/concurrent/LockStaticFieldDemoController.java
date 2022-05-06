package com.wjy.simple.concurrent;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月11日 15:17:00
 */
@RestController
@RequestMapping("lockscope")
public class LockStaticFieldDemoController {

    @GetMapping("wrong")
    public int wrong(@RequestParam(value = "count", required = false, defaultValue = "100000") int count) {
        Data.reset();
        //多线程循环一定次数调用Data类不同实例的wrong方法
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data().wrong());
        return Data.getCounter();
    }

}

class Data {
    @Getter
    private static int counter = 0;

    public static int reset() {
        counter = 0;
        return counter;
    }

    public synchronized void wrong() {
        counter++;
    }
}
