/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author wangjiayou 2019/9/3
 * @version ORAS v1.0
 */
public class ConvertStreamToArray {
    public static void main(String[] args) {
        @SuppressWarnings("unchecked")
        List<Integer> list = new ArrayList<>();
        for(int i = 1; i< 10; i++){
            list.add(i);
        }

        System.out.println(list);
        Stream<Integer> stream = list.stream();
        Integer[] array = stream.filter(i -> i%2 == 0).toArray(Integer[]::new);
        for(Integer i : array) {
            System.out.println(i);
        }
    }
}
