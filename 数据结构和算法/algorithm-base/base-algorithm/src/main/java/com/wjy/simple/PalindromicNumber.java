package com.wjy.simple;

import java.util.ArrayList;
import java.util.List;

/**
 * 回文数字解法
 */
class PalindromicNumber {
    public static void main(String[] args) {
        System.out.println(isPalindrome(1001));
    }

    private static boolean isPalindrome(int x) {
        if(x < 0 || x == 10) {
            return false;
        }
        if(x >= 0 && x < 10) {
            return true;
        }
        if(x % 10 == 0) {
            return false;
        }
        int tmp = 0;
        int remainder = 0;
        for(;;) {
            remainder = x % 10;
            tmp = tmp * 10 + remainder;
            if(x < tmp) {
                return false;
            }
            if(x == tmp || (x = x / 10) == tmp) {
                return true;
            }
        }
    }
}