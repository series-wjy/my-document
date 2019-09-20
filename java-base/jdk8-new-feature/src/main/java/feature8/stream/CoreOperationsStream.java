/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author wangjiayou 2019/9/3
 * @version ORAS v1.0
 */
public class CoreOperationsStream {
    public static void main(String[] args) {
//        sequentialStream();
        parallelStream();
    }

    private static void sequentialStream() {
        Collection<String> list = getStringList();
        Consumer action = System.out::println;

        // intermediate operations
        // filter()
        System.out.println("==============filter()=========================");
        list.stream().filter(x -> x.startsWith("A")).forEach(action);
        // map()
        System.out.println("==============map()=========================");
        list.stream().filter(x -> x.startsWith("A")).map(String::toUpperCase).forEach(action);
        // sorted()
        System.out.println("==============sorted()=========================");
        list.stream().map(String::toUpperCase).sorted().forEach(action);

        //terminal operations
        // feature8.foreach()
        System.out.println("==============feature8.foreach()=========================");
        list.stream().forEach(action);
        // collect()
        System.out.println("==============collect()=========================");
        String s = list.stream().filter(x -> x.startsWith("S")).collect(Collectors.joining());
        System.out.println(s);
        // match()
        System.out.println("==============match()=========================");
        boolean matchResult = list.stream().allMatch(x -> x.startsWith("A"));
        System.out.println(matchResult);
        matchResult = list.stream().anyMatch(x -> x.startsWith("A"));
        System.out.println(matchResult);
        matchResult = list.stream().noneMatch(String::isEmpty);
        System.out.println(matchResult);

        // match()
        System.out.println("==============count()=========================");
        long counts = list.stream().filter(x -> x.startsWith("A")).count();
        System.out.println(counts);

        // reduce()
        System.out.println("==============reduce()=========================");
        Optional<String> reduce = list.stream().reduce((x1, x2) -> x1 + "#" + x2);
        reduce.ifPresent(action);

        // findFirst()
        System.out.println("==============findFirst()=========================");
        Optional<String> s1 = list.stream().filter(x -> x.startsWith("S")).findFirst();
        System.out.println(s1.get());
    }

    private static void parallelStream() {
        Collection<Integer> list = getIntegerList();

        Optional reduce = list.parallelStream().filter(x -> x % 2 == 0).reduce((x1, x2) -> x1 + x2);
        Consumer action = System.out::println;
        reduce.ifPresent(action);

        Integer[] arr = list.parallelStream().filter(x -> x %2 == 0).toArray(Integer[]::new);
        System.out.println(Arrays.toString(arr));
    }

    private static Collection getIntegerList() {
        List<Integer> memberNames = new ArrayList<>();
        for(int i = 1; i <= 10; i ++) {
            memberNames.add(i);
        }
        return memberNames;
    }

    private static Collection getStringList() {
        List<String> memberNames = new ArrayList<>();
        memberNames.add("Amitabh");
        memberNames.add("Shekhar");
        memberNames.add("Aman");
        memberNames.add("Helmos");
        memberNames.add("Shahrukh");
        memberNames.add("Salman");
        memberNames.add("Yana");
        memberNames.add("Lokesh");
        return memberNames;
    }
}
