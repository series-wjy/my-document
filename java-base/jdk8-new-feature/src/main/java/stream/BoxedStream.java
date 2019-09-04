/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package stream;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wangjiayou 2019/9/4
 * @version ORAS v1.0
 */
public class BoxedStream {
    public static void main(String[] args) {
        // error
//        IntStream.of(1,2,3,4,5).collect(Collectors.toList());

        // boxed()
        System.out.println("==============boxed()=========================");
        List<Integer> list = IntStream.of(1,2,3,4,5).boxed().collect(Collectors.toList());
        list.forEach(System.out::println);

        System.out.println("==============max()=========================");
        Optional<Integer> max = IntStream.of(1,2,3,4,5).boxed().max(Integer::compareTo);
        System.out.println(max);
    }
}
