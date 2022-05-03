package com.wjy.simple;

import java.util.ArrayList;
import java.util.List;

class MinimumTotal {
    public static void main(String[] args) {
        List<List<Integer>> list = new ArrayList<>();
        List<Integer> list1 = new ArrayList<>();
        list1.add(-1);

        List<Integer> list2 = new ArrayList<>();
        list2.add(3);
        list2.add(2);

        List<Integer> list3 = new ArrayList<>();
        list3.add(-3);
        list3.add(1);
        list3.add(-1);

        List<Integer> list4 = new ArrayList<>();
        list4.add(4);
        list4.add(1);
        list4.add(8);
        list4.add(3);

        list.add(list1);
        list.add(list2);
        list.add(list3);
//        list.add(list4);

        int res = minimumTotal(list);
        System.out.println(res);
        System.out.println(Integer.bitCount(15));
    }

    public static int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[] mid = new int[n + 1];
        for(int i = n - 1; i >= 0; i --) {
            List<Integer> row = triangle.get(i);
            for(int j = 0; j <= i; j ++) {
                mid[j] = Math.min(mid[j], mid[j + 1]) + row.get(j);
            }
        }
        return mid[0];
    }
}