/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package concurrency;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.sound.midi.Soundbank;

/**
 * @author wangjiayou 2019/9/11
 * @version ORAS v1.0
 */
public class ThreadingAndSchedulers {
    public static void main(String[] args) throws InterruptedException {
        final Mono<String> mono = Mono.just("hello ");
        System.out.println("===========================new thread============================");
        new Thread(() -> mono
                .map(msg -> msg + "thread ")
                .subscribe(v ->
                        System.out.println(v + Thread.currentThread().getName())
                )
        , "new Thread").start();
        Thread.sleep(200);

        System.out.println("===========================publishOn============================");
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
        final Flux<String> flux = Flux
                .range(1, 5)
                .map(i -> 10 + i)
                .publishOn(s)
                .map(i -> "value " + i);

        new Thread(() -> {
            flux.subscribe(x -> {
                System.out.println(Thread.currentThread().getName() + " " + x);
            });
        },"publishOn Thread").start();
        Thread.sleep(200);

        System.out.println("===========================subscribeOn============================");
        final Flux<String> flux2 = Flux
                .range(1, 2)
                .map(i -> 10 + i)
                .subscribeOn(s)
                .map(i -> "value " + i);

        new Thread(() -> {
            flux.subscribe(x -> {
                System.out.println(Thread.currentThread().getName() + " " + x);
            });
            System.out.println(Thread.currentThread().getName());
        }).start();
    }
}
