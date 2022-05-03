package com.wjy.sort;

import java.util.Arrays;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月16日 22:27:00
 */
public class InsertSort {
    public static void main(String[] args) {
        int[] nums = new int[] {1,2,5,9,0,1};
        sortArray(nums);
        System.out.println(Arrays.toString(nums));
    }

    public static int[] sortArray(int[] nums) {
        int tmp = 0;
        for(int i = 1; i < nums.length; i ++) {
            for(int j = i - 1; j >= 0; j --) {
                if(nums[j] > nums[j + 1]) {
                    tmp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = tmp;
                }
            }
        }
        return nums;
    }
}
