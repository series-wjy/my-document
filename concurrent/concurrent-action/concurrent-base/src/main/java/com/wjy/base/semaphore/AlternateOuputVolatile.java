/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.base.semaphore;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author wangjiayou 2019/8/30
 * @version ORAS v1.0
 */
public class AlternateOuputVolatile {
    static volatile int count = 0;
    static volatile boolean flag = true;
    public static void main(String[] args) throws InterruptedException {
        semaphore();
    }

    private static void semaphore() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                System.out.println("线程1准备好。。。。。。");
                latch.await();
                while (true) {
                    if(!flag) {
                        continue;
                    }
                    System.out.println(Thread.currentThread().getName() + ":" + count++);
                    flag = false;
                    if (count >= 100) {
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
                    if(flag) {
                        continue;
                    }
                    System.out.println(Thread.currentThread().getName() + ":" + count++);
                    flag = true;
                    if (count >= 100) {
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
