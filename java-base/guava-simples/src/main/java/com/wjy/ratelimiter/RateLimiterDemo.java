package com.wjy.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RateLimiterDemo {
    static long prev = System.nanoTime();
    public static void main(String[] args) {
        RateLimiter limiter = RateLimiter.create(2.0);

        ExecutorService es = Executors.newFixedThreadPool(1);

        for(int i = 0; i < 5; i ++) {
            limiter.acquire();
            es.execute(new Runnable() {
                public void run() {
                    long cur = System.nanoTime();
                    System.out.println((cur - prev) / 1000_000);
                    prev = cur;
                }
            });
        }
    }
}
