package com.wjy.experience.concurrent;

import com.wjy.experience.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 多次加锁导致死锁示例
 *
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月22日 17:24:00
 */
@Slf4j
@RestController
@RequestMapping("deadlock")
public class DeadLockDemo {

    // 商品列表
    private static Map<String, Item> items = new HashMap<>();

    static {
        // 初始化商品列表
        IntStream.rangeClosed(0, 9).forEach((i) -> {
            Item item = new Item("item" + i);
            items.put("item" + i, item);
        });
    }

    /**
     * 死锁示例，获取到的商品是无序的，所以加锁的顺序也不一致
     * @return
     */
    @GetMapping("defective")
    public long wrong() {
        long begin = System.currentTimeMillis();
        //并发进行100次下单操作，统计成功次数
        long success = IntStream.rangeClosed(1, 100).parallel()
                .mapToObj(i -> {
                    List<Item> cart = createCart();
                    return createOrder(cart);
                })
                .filter(result -> result)
                .count();
        log.info("success:{} totalRemaining:{} took:{}ms items:{}",
                success,
                items.entrySet().stream().map(item -> item.getValue().getRemaining()).reduce(0, Integer::sum),
                System.currentTimeMillis() - begin, items);
        return success;
    }

    private List<Item> createCart() {
        return IntStream.rangeClosed(1, 3)
                .mapToObj(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(items.size());
                    return key;
                })
                .map(name -> items.get(name)).collect(Collectors.toList());
    }

    /**
     * 将购物车中的商品排序，保证多个线程对商品的加锁顺序一致
     * @return
     */
    @GetMapping("perfect")
    public long right() {
        long begin = System.currentTimeMillis();
        //并发进行100次下单操作，统计成功次数
        long success = IntStream.rangeClosed(1, 100).parallel()
                .mapToObj(i -> {
                    List<Item> cart = createCart().stream()
                            .sorted(Comparator.comparing(Item::getName))
                            .collect(Collectors.toList());
                    return createOrder(cart);
                })
                .filter(result -> result)
                .count();
        log.info("success:{} totalRemaining:{} took:{}ms items:{}",
                success,
                items.entrySet().stream().map(item -> item.getValue().getRemaining()).reduce(0, Integer::sum),
                System.currentTimeMillis() - begin, items);
        return success;
    }

    private boolean createOrder(List<Item> order) {
        //存放所有获得的锁
        List<ReentrantLock> locks = new ArrayList<>();

        for (Item item : order) {
            try {
                //获得锁10秒超时
                if (item.getLock().tryLock(10, TimeUnit.SECONDS)) {
                    locks.add(item.getLock());
                } else {
                    locks.forEach(ReentrantLock::unlock);
                    return false;
                }
            } catch (InterruptedException e) {
            }
        }
        //锁全部拿到之后执行扣减库存业务逻辑
        try {
            order.forEach(item -> item.setRemaining(item.getRemaining() - 1));
        } finally {
            locks.forEach(ReentrantLock::unlock);
        }
        return true;
    }
}
