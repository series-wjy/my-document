package com.gblw.instrument.future;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月06日 23:58:00
 */
public class CompletableFutureSubmitTaskTest {

    @Test
    public void test2() throws Exception {
        ForkJoinPool pool = new ForkJoinPool();
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread() + " start,time->" + System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            if (true) {
                throw new RuntimeException("test");
            } else {
                System.out.println(Thread.currentThread() + " exit,time->" + System.currentTimeMillis());
                return 1.2;
            }
        }, pool);
        System.out.println("main thread start,time->" + System.currentTimeMillis());
        //等待子任务执行完成
        System.out.println("run result->" + cf.get());
        System.out.println("main thread exit,time->" + System.currentTimeMillis());
    }

    public static void main(String[] args) {
        CompletableFuture<Integer> test = test();
        try {
            Integer integer = test.get();
            System.out.println(integer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {           //让main线程 晚会儿退出 控制台就会打印了
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<Integer> test() {
        CompletableFuture<Integer> t1 = CompletableFuture.supplyAsync(() -> {
            // 为何加了睡眠，所有语句都不打印了？
            try {
                Thread.sleep(99);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("======执行操作 A======= ");
            return 100;
        });
        return t1;
    }
}
