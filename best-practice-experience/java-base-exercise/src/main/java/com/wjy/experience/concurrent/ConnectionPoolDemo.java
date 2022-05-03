package com.wjy.experience.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 连接池示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月23日 16:22:00
 */
@RestController
@RequestMapping("connection-pool")
@Slf4j
public class ConnectionPoolDemo {
    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        try (Jedis jedis = new Jedis("192.168.56.101", 6379)) {
            Assert.isTrue("OK".equals(jedis.set("a", "1")), "set a = 1 return OK");
            Assert.isTrue("OK".equals(jedis.set("b", "2")), "set b = 2 return OK");
        }

        jedisPool = new JedisPool("192.168.56.101",6379);
        // 定义关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { jedisPool.close(); }));
    }

    @GetMapping("redis-defective")
    public void redisConnectionPoolDefective() throws InterruptedException {

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

    @GetMapping("redis-perfect")
    public void redisConnectionPoolPerfect() throws InterruptedException {

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                String result = jedisPool.getResource().get("a");
                if (!result.equals("1")) {
                    log.warn("Expect a to be 1 but found {}", result);
                    return;
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                String result = jedisPool.getResource().get("b");
                if (!result.equals("2")) {
                    log.warn("Expect b to be 2 but found {}", result);
                    return;
                }
            }
        }).start();
        TimeUnit.SECONDS.sleep(5);
    }
}
