package com.gblw.instrument.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * CompletableFuture 简单用法
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月03日 22:43:00
 */
public class CompletableFutureSimpleDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.complete("This is a test result!");
        String result = completableFuture.get();
        System.out.println(result);
    }
}
