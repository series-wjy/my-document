/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wangjiayou 2019/9/3
 * @version ORAS v1.0
 */
public class CoreOperationsStream {
    public static void main(String[] args) {
        Collection<String> list = get();

        // intermediate operations
        // filter()
        System.out.println("==============filter()=========================");
        Consumer action = System.out::println;
        list.stream().filter(x -> x.startsWith("A")).forEach(action);
        // map()
        System.out.println("==============map()=========================");
        list.stream().filter(x -> x.startsWith("A")).map(String::toUpperCase).forEach(action);
        // sorted()
        System.out.println("==============sorted()=========================");
        list.stream().map(String::toUpperCase).sorted().forEach(action);

        //terminal operations
        // foreach()
        System.out.println("==============foreach()=========================");
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
    }

    private static Collection get() {
        List<String> memberNames = new ArrayList<>();
        memberNames.add("Amitabh");
        memberNames.add("Shekhar");
        memberNames.add("Aman");
        memberNames.add("Rahul");
        memberNames.add("Shahrukh");
        memberNames.add("Salman");
        memberNames.add("Yana");
        memberNames.add("Lokesh");
        return memberNames;
    }
}
