package com.wjy.simple;

class ReverseWords {
    public static void main(String[] args) {
        System.out.println(reverseWords("Let's take LeetCode contest"));
    }

    public static String reverseWords(String s) {
        int low = 0, high = 0, n = s.length();
        char[] ch = new char[n];
        s.getChars(0, n, ch, 0);
        while(low < n) {
            if(high == n || ch[high] == ' ') {
                int tmp = high;
                while(low < tmp) {
                    char swap = ch[low];
                    ch[low] = ch[tmp - 1];
                    ch[tmp - 1] = swap;
                    tmp--;
                    low++;
                }
                high += 1;
                low = high;
                continue;
            }
            high++;
        }
        return new String(ch);
    }
}