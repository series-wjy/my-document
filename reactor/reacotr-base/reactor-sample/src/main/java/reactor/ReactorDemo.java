/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class ReactorDemo {
    public static void main(String[] args) {
        Flux<String> ids = ifhrIds();
        Flux<String> combinations =
                ids.flatMap(id -> {
                    Mono<String> nameTask = ifhrName(id);
                    Mono<Integer> statTask = ifhrStat(id);

                    return nameTask.zipWith(statTask,
                            (name, stat) -> "Name " + name + " has stats " + stat);
                });

        Mono<List<String>> result = combinations.collectList();

        List<String> results = result.block();
        System.out.println(results);

        Mono<String> noData = Mono.empty();

        Mono<String> data = Mono.just("foo");

        Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3);
        System.out.println(numbersFromFiveToSeven.collectList().block());
    }

    private static Mono<Integer> ifhrStat(String id) {
        return Mono.just(Integer.valueOf(id));
    }

    private static Mono<String> ifhrName(String id) {
        return Mono.just(id);
    }

    private static Flux<String> ifhrIds() {
        return Flux.just("1", "2", "3");
    }
}
