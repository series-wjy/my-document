package com.wjy.sort;

import java.util.Arrays;

/**
 * 分为排序区间和非排序区间，选择非排序区间最小的数进行比较
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月16日 22:27:00
 */
public class SelectSort {
    public static void main(String[] args) {
        int[] nums = new int[] {1,2,5,9,0,1};
        sortArray(nums);
        System.out.println(Arrays.toString(nums));
    }

    public static int[] sortArray(int[] nums) {
        for(int i = 0; i < nums.length - 1; i ++) {
            int j = i + 1;
            int tmp = nums[j];
            int min = j;
            for(; j <nums.length; j ++) {
                if(nums[j] < tmp) {
                    tmp = nums[j];
                    min = j;
                }
            }
            nums[min] = nums[i];
            nums[i] = tmp;
        }
        return nums;
    }
}
