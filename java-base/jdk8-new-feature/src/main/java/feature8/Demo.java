package feature8;/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author wangjiayou 2019/9/5
 * @version ORAS v1.0
 */
public class Demo {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        //ArrayList<Integer> list = new ArrayList<>(Arrays.asList(10,20,30,null));
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(null);

        reset(list);
        setOne(list);
        Optional<Integer> reduce = list.stream().reduce((x1, x2) -> x1 + x2);
        System.out.println(reduce);
        System.out.println(list);
    }

    private static void reset(List<Integer> list) {
        System.out.println(list);
        list.subList(2,4).set(0,50);
        System.out.println(list);
        list = new ArrayList<>(list);
        System.out.println(list);
        list.add(20);
        System.out.println(list);
    }

    private static void setOne(List<Integer> list) {
        list.set(3, 80);
    }
}
