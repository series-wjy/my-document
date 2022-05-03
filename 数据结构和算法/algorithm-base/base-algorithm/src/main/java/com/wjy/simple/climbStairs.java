package com.wjy.simple;

class climbStairs {
    public static void main(String[] args) {
        climbStairs solution = new climbStairs();
        solution.climbStairs(45);
    }
    public int climbStairs(int n) {
        if(n == 1) {
            return 1;
        }
        if(n == 2) {
            return 2;
        }
        int res = climbStairs(n - 1) + climbStairs(n - 2);
        return res;
    }
}