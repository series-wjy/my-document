package com.wjy.bitopt;

import java.util.Arrays;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年02月24日 11:49:00
 */
public class SwapTwoNumber {

    public static void main(String[] args) {
        String s = "";
        System.out.println(1/2);
        int[] nums = new int[]{-5, 9};
        swapTwoNum(nums[0], nums[1]);
        System.out.println(Arrays.toString(nums));
    }

    private static void swapTwoNum(int a, int b) {
        a ^= b;
        b ^= a;
        a ^= b;
    }
}
