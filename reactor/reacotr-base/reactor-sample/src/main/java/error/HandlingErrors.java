/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package error;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @author wangjiayou 2019/9/11
 * @version ORAS v1.0
 */
public class HandlingErrors {
    public static void main(String[] args) throws InterruptedException {
        Flux flux = Flux.just(1, 2, 0)
                .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
                .onErrorReturn("Divided by zero :("); // error handling example
        flux.subscribe(System.out::println);


        Flux<Integer> s = Flux.range(1, 10)
                .map(v -> doSomethingDangerous(v))
                .map(v -> doSecondTransform(v));
        s.subscribe(value -> System.out.println("RECEIVED " + value),
                error -> System.err.println("CAUGHT " + error)
        );

        Flux.interval(Duration.ofMillis(250))
                .map(input -> {
                    if (input < 3) return "tick " + input;
                    throw new RuntimeException("boom");
                })
                .retry(1)
                .elapsed()
                .subscribe(System.out::println, System.err::println);

        Thread.sleep(2100);
    }

    public void test() {
        Flux.just("10")
                .map(this::doSomethingDangerous2)
                .onErrorReturn("RECOVERED");
        Flux.just("10")
                .map(this::doSomethingDangerous2)
                .onErrorReturn(e -> e.getMessage().equals("boom10"), "recovered10");
    }

    private static Integer doSecondTransform(Integer v) {
        //System.out.println("intermediate value " + v);
        return v;
    }

    private static Integer doSomethingDangerous(Integer v) {
        System.out.println("intermediate value " + v);
        int i = 1 / (v - 3);
        return i;
    }

    private  String doSomethingDangerous2(String v) {
        System.out.println("intermediate value " + v);
        return v;
    }
}
