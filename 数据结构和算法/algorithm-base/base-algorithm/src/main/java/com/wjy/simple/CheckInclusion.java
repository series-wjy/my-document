package com.wjy.simple;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class CheckInclusion {
    public static void main(String[] args) {
        System.out.println(checkInclusion("ab","eidboaoo"));
    }

    public static boolean checkInclusion(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        if(n > m) {
            return false;
        }
        int[] cnt = new int[26];
        for(int i = 0; i < n; i ++) {
            --cnt[s1.charAt(i) - 'a'];
        }
        int left = 0, right = 0;
        while(right < m) {
            int x = s2.charAt(right) - 'a';
            ++cnt[x];
            while(cnt[x] > 0) {
                --cnt[s2.charAt(left) - 'a'];
                left++;
            }
            if(right - left + 1 == n) {
                return true;
            }
            right++;
        }
        return false;
    }
}