package com.gblw.instrument.future;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * thenRun、thenAccept、thenApply 用法示例
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月02日 21:22:43
 */
public class CompletableFutureCallbackDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future1 = testThenRun();
        CompletableFuture<Void> future2 = testThenAccept();
        CompletableFuture<String> future3 = testThenApply();
        future1.get();
        future2.get();
        String result = future3.get();
        System.out.println(result);
    }

    private static CompletableFuture<Void> testThenRun() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("test thenRun task is running......");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("test thenRun task is complete!");
        }).thenRun(() -> {
            System.out.println("execute thenRun callback!");
        });
        return future;
    }

    private static CompletableFuture<Void> testThenAccept() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("test thenAccept task is running......");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new IllegalArgumentException(e);
            }
            return "thenAccept task running complete!";
        }).thenAccept(res -> {
            System.out.println("accept previous result: " + res);
        });
        return future;
    }

    private static CompletableFuture<String> testThenApply() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("test thenApply task is running......");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new IllegalArgumentException(e);
            }
            return "thenApply task running complete!";
        }).thenApply(res -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + " " + res;
        });
        return future;
    }
}
