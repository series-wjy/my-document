package com.wjy.simple;

class MaxSubArray {

    public static void main(String[] args) {
        int[] nums = {5,4,-1,-2,-6,9};
        maxSubArray(nums);
    }

    public static int maxSubArray(int[] nums) {
        int pre = 0, maxAns = nums[0];
        for (int x : nums) {
            pre = Math.max(pre + x, x);
            maxAns = Math.max(maxAns, pre);
        }
        return maxAns;
    }
}