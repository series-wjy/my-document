/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package concurrent.lock;

import concurrent.oop.SafeWM2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wangjiayou 2019/10/10
 * @version ORAS v1.0
 */
public class SimpleLockDemo {

    private int value;

    // 普通对象锁
    private final Lock lock = new ReentrantLock();

    // 静态对象锁
    private static final Lock lock2 = new ReentrantLock();

    public int getValue() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void addValue() {
        lock.lock();
        try {
            value = value + 1;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        SimpleLockDemo demo = new SimpleLockDemo();
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            while (true) {
                latch.countDown();
                int value = demo.getValue();
                if(value == 100) {
                    break;
                }
                demo.addValue();
            }
        }).start();
        new Thread(() -> {
            while (true) {
                latch.countDown();
                int value = demo.getValue();
                if(value == 100) {
                    break;
                }
                demo.addValue();
            }
        }).start();
        try {
            latch.await();
            System.out.println(demo.value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
