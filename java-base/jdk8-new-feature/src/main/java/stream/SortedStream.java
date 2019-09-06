/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class SortedStream {
    public static void main(String[] args) {
        System.out.println("=========================default sorted==========================");
        List<Integer> list = Arrays.asList(2, 4, 1, 3, 7, 5, 9, 6, 8);
        List<Integer> sortedList = list.stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println(sortedList);

        System.out.println("=========================comparator sorted==========================");
        List<Integer> sortedList2 = list.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        System.out.println(sortedList2);

        System.out.println("=========================custom sorted==========================");
        Comparator<Integer> reverseComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return i2.compareTo(i1);
            }
        };
        List<Integer> sortedList3 = list.stream()
                .sorted(reverseComparator)
                .collect(Collectors.toList());
        System.out.println(sortedList3);

        System.out.println("=========================lambda sorted==========================");
        List<Integer> sortedList4 = list.stream()
                .sorted((x1, x2) -> x2.compareTo(x1))
                .collect(Collectors.toList());
        System.out.println(sortedList4);
    }
}
