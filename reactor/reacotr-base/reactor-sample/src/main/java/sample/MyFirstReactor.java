/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package sample;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class MyFirstReactor {
    public static void main(String[] args) {
        List<Integer> list1 = new ArrayList<>(Arrays.asList(1,2,3));
        List<Integer> list2 = new ArrayList<>(Arrays.asList(4,5,6));

        list2.stream().flatMap(item -> list2.stream().filter(x -> x%2==0)).forEach(System.out::println);
    }

    public static void reactor() {
        Hooks.onOperatorDebug();
        Mono<Long> totalTxtSize = Flux
                .just("/tmp", "/home", "/404")
                .map(File::new)
                .concatMap(file -> Flux.just(file.listFiles()))
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".txt"))
                .map(File::length)
                .reduce(0L, Math::addExact);

        totalTxtSize.subscribe(System.out::println);
    }
}
