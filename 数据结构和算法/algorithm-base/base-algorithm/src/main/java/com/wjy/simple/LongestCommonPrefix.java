package com.wjy.simple;

class LongestCommonPrefix {
    public static void main(String[] args) {
        System.out.println(longestCommonPrefix(new String[]{"flower","alow","flight"}));
    }

    public static String longestCommonPrefix(String[] strs) {
        String result = "";
        for(int i = 0; i < strs[0].length(); i ++) {
            boolean isMatch = true;
            String ch = strs[0].substring(i, i + 1);
            for(int j = 1; j < strs.length; j ++) {
                if(!ch.equals(strs[j].substring(i, i + 1))) {
                    isMatch = false;
                    return result;
                }
            }
            if(isMatch) {
                result += ch;
            }
        }
        return result;
    }
}