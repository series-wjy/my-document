/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package completablefuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class CompletableFutureDemo {

    public static void main(String[] args) {
        CompletableFuture<List<String>> ids = ifhIds();

        CompletableFuture<List<String>> result = ids.thenComposeAsync(l -> {
            Stream<CompletableFuture<String>> zip =
                    l.stream().map(i -> {
                        CompletableFuture<String> nameTask = ifhName(i);
                        CompletableFuture<Integer> statTask = ifhStat(i);

                        return nameTask.thenCombineAsync(statTask, (name, stat) -> "Name " + name + " has stats " + stat);
                    });
            List<CompletableFuture<String>> combinationList = zip.collect(Collectors.toList());
            CompletableFuture<String>[] combinationArray = combinationList.toArray(new CompletableFuture[combinationList.size()]);

            CompletableFuture<Void> allDone = CompletableFuture.allOf(combinationArray);
            return allDone.thenApply(v -> combinationList.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
        });

        List<String> results = result.join();
        System.out.println(results);
    }

    private static CompletableFuture<Integer> ifhStat(String i) {
        return CompletableFuture.supplyAsync(() -> {
            return Integer.parseInt(i);
        });
    }

    private static CompletableFuture<String> ifhName(String i) {
        return CompletableFuture.supplyAsync(() -> {
            return i;
        });
    }

    private static CompletableFuture<List<String>> ifhIds() {
        return CompletableFuture.supplyAsync(() -> {
            return Arrays.asList("1", "2", "3");
        });
    }
}
