package com.wjy.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月13日 10:36:00
 */
@RestController
@Slf4j
public class RedisConnectionPoolDemoController {
    private static JedisPool jedisPool = new JedisPool("192.168.56.101", 6379);

    @PostConstruct
    public void init() {
        try (Jedis jedis = new Jedis("192.168.56.101", 6379)) {
            Assert.isTrue("OK".equals(jedis.set("a", "1")), "set a = 1 return OK");
            Assert.isTrue("OK".equals(jedis.set("b", "2")), "set b = 2 return OK");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jedisPool.close();
        }));
    }

    public static void main(String[] args) throws InterruptedException {
        // 非线程安全的 redis 连接使用
        //noThreadsafe();
        // 线程安全的 redis 连接池
        threadSafe();
        return;
    }

    public static void threadSafe() {
        new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                for (int i = 0; i < 1000; i++) {
                    String result = jedis.get("a");
                    if (!result.equals("1")) {
                        log.warn("Expect a to be 1 but found {}", result);
                        return;
                    }
                }
            }
        }).start();
        new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                for (int i = 0; i < 1000; i++) {
                    String result = jedis.get("b");
                    if (!result.equals("2")) {
                        log.warn("Expect b to be 2 but found {}", result);
                        return;
                    }
                }
            }
        }).start();
    }

    private static void noThreadsafe() throws InterruptedException {
        Jedis jedis = new Jedis("192.168.56.101", 6379);
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                String result = jedis.get("a");
                if (!result.equals("1")) {
                    log.warn("Expect a to be 1 but found {}", result);
                    return;
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                String result = jedis.get("b");
                if (!result.equals("2")) {
                    log.warn("Expect b to be 2 but found {}", result);
                    return;
                }
            }
        }).start();
        TimeUnit.SECONDS.sleep(5);
    }
}
