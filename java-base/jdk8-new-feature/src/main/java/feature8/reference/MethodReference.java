/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author wangjiayou 2019/9/4
 * @version ORAS v1.0
 */
public class MethodReference {

    public static void main(String[] args) {
        //Math::max == Math.max();

        //System.out.println() == System.out::println;

        //String::length == str.length();

        //ArrayList::new == new ArrayList();

        Consumer action = System.out::println;

        List<Integer> integers = Arrays.asList(1,12,433,5);
        Optional<Integer> max = integers.stream().reduce( Math::max );
        max.ifPresent(action);

        List<String> strings = Arrays
                .asList("how", "to", "do", "in", "java", "dot", "com");

        List<String> sorted = strings.stream().sorted((s1, s2) -> s1.compareTo(s2))
                .collect(Collectors.toList());
        System.out.println(sorted);
        List<String> sortedAlt = strings.stream().sorted(String::compareTo)
                .collect(Collectors.toList());
        System.out.println(sortedAlt);

        sorted = strings.stream().sorted(String::compareTo).filter(x -> x.contains("o"))
                .collect(Collectors.toCollection(ArrayList::new));
        System.out.println(sorted);
    }
}
