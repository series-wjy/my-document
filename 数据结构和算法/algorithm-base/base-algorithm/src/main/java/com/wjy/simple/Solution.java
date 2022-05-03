package com.wjy.simple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public static void main(String[] args) {
        int[] nums1 = {4,9,5};
        int[] nums2 = {9,4,9,8,4};
        intersect(nums1, nums2);
    }

    public static int[] intersect(int[] nums1, int[] nums2) {
        Set<Integer> set = new HashSet<>();
        for(int a : nums1) {
            set.add(a);
        }
        List<Integer> list = new ArrayList<>();
        for(int b : nums2) {
            if(!set.add(b)) {
                list.add(b);
            }
        }
        int[] res = new int[list.size()];
        for(int i = 0; i < list.size(); i ++) {
            res[i] = list.get(i);
        }
        return res;
    }
}