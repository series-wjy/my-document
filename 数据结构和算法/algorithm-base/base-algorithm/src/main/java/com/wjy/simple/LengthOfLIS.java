package com.wjy.simple;

class LengthOfLIS {

    public static void main(String[] args) {
        LengthOfLIS lengthOfLIS = new LengthOfLIS();
        int[] nums = new int[]{0,1,0,3,2,3};
        lengthOfLIS.lengthOfLIS(nums);
    }
    public int lengthOfLIS(int[] nums) {
        int[] dp = new int[nums.length];
        for(int i = 0; i < nums.length; i ++) {
            dp[i] = 1;// 初始化状态记录
        }
        int max = 0;
        for(int i = 1; i < nums.length; i ++) {
            for(int j = 0; j < i; j ++) {
                if(nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[j] + 1, dp[i]);
                }
            }
        }
        int res = 0;
        for(int i = 0; i < nums.length; i ++) {
            res = Math.max(res, dp[i]);
        }
        return res;
    }
}