package com.wjy.simple;

import java.util.ArrayList;
import java.util.List;

class LetterCasePermutation {

    public static void main(String[] args) {
        LetterCasePermutation solution = new LetterCasePermutation();
        solution.letterCasePermutation("a1b1c1");
    }

    public List<String> letterCasePermutation(String s) {
        // char a ^=1 << 5; 大小写切换
        char[] arrs = s.toCharArray();
        List<String> res = new ArrayList<>();
        dfs(arrs, 0, res);
        return res;
    }

    private void dfs(char[] arrs, int index,  List<String> res) {
        if(index == arrs.length) {
            res.add(new String(arrs));
            return;
        }
        dfs(arrs, index + 1, res);
        if(Character.isLetter(arrs[index])) {
            arrs[index] ^= 1 << 5;
            dfs(arrs, index + 1, res);
        }
    }
}