package com.gblw.instrument.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * runAsync、supplyAsync 用法示例
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月02日 20:32:11
 */
public class CompletableFutureSubmitTaskDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future1 = testRunAsync();
        CompletableFuture<String> future2 = testSupplyAsync();
        future1.get();
        String s = future2.get();
        System.out.println(s);
    }

    private static CompletableFuture<Void> testRunAsync() {
        System.out.println("============= start testRunAsync() ==============");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("test runAsync task is running......");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return future;
    }

    private static CompletableFuture<String> testSupplyAsync() {
        System.out.println("============= start testSupplyAsync() ==============");
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("test supplyAsync task is running......");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalArgumentException(e);
            }
            return "supplyAsync task running complete!";
        });
        return future;
    }
}
