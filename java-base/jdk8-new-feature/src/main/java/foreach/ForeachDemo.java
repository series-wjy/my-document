/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package foreach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author wangjiayou 2019/9/3
 * @version ORAS v1.0
 */
public class ForeachDemo {
    public static void main(String[] args) {
        stream();
        useList();
        useMap();
        customAction();
    }

    public static void stream() {
        System.out.println("======================stream foreach======================");
        ArrayList<Integer> numberList = new ArrayList<>(Arrays.asList(1,2,3,4,5));
        Consumer<Integer> action = System.out::println;
        numberList.stream().filter(n -> n%2  == 0).forEach( action );
    }

    public static void useList() {
        System.out.println("======================useList foreach======================");
        ArrayList<Integer> numberList = new ArrayList<>(Arrays.asList(1,2,3,4,5));
        Consumer<Integer> action = System.out::println;
        numberList.forEach(action);
    }

    public static void useMap() {
        System.out.println("======================useMap foreach======================");
        HashMap<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        //1. Map entries
        Consumer<Map.Entry<String, Integer>> action = System.out::print;
        map.entrySet().forEach(action);

        //2. Map keys
        Consumer<String> actionOnKeys = System.out::println;
        map.keySet().forEach(actionOnKeys);

        //3. Map values
        Consumer<Integer> actionOnValues = System.out::println;
        map.values().forEach(actionOnValues);
    }

    public static void customAction() {
        System.out.println("======================custome action foreach======================");
        HashMap<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        Consumer<Map.Entry<String, Integer>> action = stringIntegerEntry -> {
            System.out.println("key=" + stringIntegerEntry.getKey());
            System.out.println("value=" + stringIntegerEntry.getValue());
        };
        map.entrySet().forEach(action);
    }
}
