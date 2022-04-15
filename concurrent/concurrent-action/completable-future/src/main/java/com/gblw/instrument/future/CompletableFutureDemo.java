package com.gblw.instrument.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月06日 23:06:00
 */
public class CompletableFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Thread mainThread = Thread.currentThread();
        ExecutorService executor = new ThreadPoolExecutor(10, 10,
                0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executor.submit(() -> {
            System.out.println("pool start......");
        });
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
//                Thread.sleep(1000);
                //prints false
                System.out.println("Main thread is alive: " + mainThread.isAlive());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }, executor)
                .thenAccept(taskResult -> {
                    System.out.println("LongRunning is finished");
                    //executor.shutdown();
                });
        System.out.println("wait completable......");
        future.get();
    }
}
