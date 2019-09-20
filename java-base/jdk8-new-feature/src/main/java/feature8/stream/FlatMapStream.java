/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class FlatMapStream {
    public static void main(String[] args) {
        System.out.println("===================Convert list of lists to single list===========================");
        List<Integer> list1 = Arrays.asList(1,2,3);
        List<Integer> list2 = Arrays.asList(4,5,6);
        List<Integer> list3 = Arrays.asList(7,8,9);

        List<List<Integer>> listOfLists = Arrays.asList(list1, list2, list3);
        List<Integer> listOfAllIntegers = listOfLists.stream()
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
        System.out.println(listOfAllIntegers);

        System.out.println("===================Convert array of arrays to single list===========================");
        String[][] dataArray = new String[][]{{"a", "b"}, {"c", "d"}, {"e", "f"}, {"g", "h"}};
        List<String> listOfAllChars = Arrays.stream(dataArray)
                .flatMap(x -> Arrays.stream(x))
                .collect(Collectors.toList());
        System.out.println(listOfAllChars);
    }
}
