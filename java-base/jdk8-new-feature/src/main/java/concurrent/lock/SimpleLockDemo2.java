/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package concurrent.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wangjiayou 2019/10/10
 * @version ORAS v1.0
 */
public class SimpleLockDemo2 {

    private int value;

    // 普通对象锁
    private final Lock lock = new ReentrantLock();

    // 静态对象锁
    private static final Lock lock2 = new ReentrantLock();

    public int getValue() throws InterruptedException {
        lock.lock();
        try {
            System.out.println("entry getValue()......");
            Thread.sleep(5000);
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void addValue() {
        lock.lock();
        try {
            System.out.println("entry addValue()......");
            value = value + 1;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        SimpleLockDemo2 demo = new SimpleLockDemo2();
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            try {
                int value = demo.getValue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            demo.addValue();
        }).start();
    }
}
