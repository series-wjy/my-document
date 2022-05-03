package com.wjy.simple;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Combine {
    public static void main(String[] args) {
        int x = 1<<5;
        int a = 65;
        System.out.println(Integer.toBinaryString(65));
        System.out.println(Integer.toBinaryString(32));
        System.out.println(Integer.toBinaryString(97));
        System.out.println(a);
        System.out.println(a ^= x);

        char b = 97;
        System.out.println(a);

        Combine obj = new Combine();
        List<List<Integer>> combine = obj.combine(4, 2);
        System.out.println(combine);
    }

    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> list = new ArrayList<>();
        Deque<Integer> midQueue = new ArrayDeque<>();
        dfs(n, k, 1, midQueue, list);
        return list;
    }

    private Deque<Integer> dfs(int n, int k, int begin, Deque<Integer> midQueue, List<List<Integer>> list) {
        if(midQueue.size() == k) {
            list.add(new ArrayList<>(midQueue));
            return midQueue;
        }
        for(int i = begin; i <= n; i ++) {
            midQueue.addLast(i);
            dfs(n, k, i + 1, midQueue, list);
            midQueue.removeLast();
        }
        return null;
    }
}