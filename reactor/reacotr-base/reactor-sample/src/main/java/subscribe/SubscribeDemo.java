/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package subscribe;

import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * @author wangjiayou 2019/9/9
 * @version ORAS v1.0
 */
public class SubscribeDemo {
    public static void main(String[] args) {
        System.out.println("===========================split line 1=================================");
        Flux<Integer> flux1 = Flux.range(1,5);
        Consumer runnable = System.out::print;
        Disposable subscribe = flux1.subscribe(System.out::println);

        System.out.println("===========================split line 2=================================");
        Flux<Integer> flux2 = Flux.range(1,5)
                .map(x -> {
                    if (x <= 3) {
                        return x;
                    }
                    throw new RuntimeException("Got to 4!");
                });
        flux2.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error: " + error));

        System.out.println("===========================split line 3=================================");
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"));

        System.out.println("===========================split 4=================================");
        Flux<Integer> ints1 = Flux.range(1, 4);
        ints1.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"),
                sub -> sub.request(3));

        System.out.println("===========================split 5=================================");
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints2 = Flux.range(1, 4);
        ints2.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> {System.out.println("Done");},
                s -> s.request(10));
        ints2.subscribe(ss);

        System.out.println("===========================split 6=================================");
        Flux.range(1, 10)
                .doOnRequest(r -> System.out.println("request of " + r))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    public void hookOnSubscribe(Subscription subscription) {
                        request(1);
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void hookOnNext(Integer integer) {
                        System.out.println("Cancelling after having received " + integer);
                        cancel();
                    }
                });
    }
}
