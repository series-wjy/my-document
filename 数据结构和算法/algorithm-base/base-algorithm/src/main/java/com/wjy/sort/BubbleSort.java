package com.wjy.sort;

import java.util.Arrays;

/**
 * 从第一个元素开始冒泡
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月16日 22:22:00
 */
public class BubbleSort {
    public static void main(String[] args) {
        int[] nums = new int[] {1,2,5,9,0,1};
        sortArray(nums);
        System.out.println(Arrays.toString(nums));
    }

    public static int[] sortArray(int[] nums) {
        int tmp = 0;
        boolean flag = false;
        while(!flag) {
            flag = true;
            for(int i = 0; i < nums.length - 1; i ++) {
                if(nums[i] > nums[i + 1]) {
                    tmp = nums[i + 1];
                    nums[i + 1] = nums[i];
                    nums[i] = tmp;
                    flag = false;
                }
            }
        }
        return nums;
    }
}
