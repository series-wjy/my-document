/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wangjiayou 2019/9/3
 * @version ORAS v1.0
 */
public class ConvertStreamToList {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for(int i = 1; i< 10; i++){
            list.add(i);
        }

        System.out.println(list);
        Stream<Integer> stream = list.stream();
        List<Integer> evenNumbersList = stream.filter(i -> i%2 == 0).collect(Collectors.toList());
        System.out.print(evenNumbersList);
    }
}
