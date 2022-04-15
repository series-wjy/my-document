package com.wjy.base.utils;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * 可重入锁使用示例
 *
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2021年09月07日 08:37:00
 */
public class ReentrantLockDemo {

    public static void main(String[] args) {
        LockForFactory factory = new LockForFactory();
        IntStream.range(0, 1).forEach((n) -> {
            Thread t = new Thread(() -> {
                for(;;) {
                    factory.produce(n);
                }
            }, "rl-produce-Thread-" + n);
            t.start();
        });

        IntStream.range(2, 3).forEach((n) -> {
            Thread t = new Thread(() -> {
                for(;;) {
                    factory.consume(n);
                }
            }, "rl-consume-Thread-" + n);
            t.start();
        });
    }

    static class LockForFactory {
        private Lock lock = new ReentrantLock();
        private Queue queue = new LinkedBlockingQueue<String>(20);

        public void produce(int i) {
            try{
                lock.lock();
                System.out.println("produce item:" + i);
            } finally {
                lock.unlock();
            }
        }

        public void consume(int i) {
            try{
                lock.lock();
                System.out.println("consume item:" + i);
            } finally {
                lock.unlock();
            }
        }
    }
}
