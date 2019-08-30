/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.base.semaphore;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangjiayou 2019/8/30
 * @version ORAS v1.0
 */
public class AlternateOuputAtomic {
    static volatile AtomicInteger count1;
    static volatile AtomicInteger count2;
    public static void main(String[] args) throws InterruptedException {
        count1 = new AtomicInteger(0);
        count2 = new AtomicInteger(1);
        output();
    }

    private static void output() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                System.out.println("线程1准备好。。。。。。");
                latch.await();
                while (true) {
                    if(count1.compareAndSet(0, 1)) {
                        System.out.println(Thread.currentThread().getName() + ":" + count2.getAndAdd(1));
                    }
                    if (count2.get() >= 100) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "线程1").start();

        new Thread(() -> {
            try {
                System.out.println("线程2准备好。。。。。。");
                latch.await();
                while (true) {
                    if(count1.compareAndSet(1, 0)) {
                        System.out.println(Thread.currentThread().getName() + ":" + count2.getAndAdd(1));
                    }
                    if (count2.get() >= 100) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程2").start();
        Thread.sleep(50);
        System.out.println("交替打印准备开始。。。。。。");
        latch.countDown();
    }
}
