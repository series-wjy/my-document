package com.wjy.base.utils;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2021年09月12日 22:34:00
 */
public class PrintBinaryDemo {
    static final int SHARED_SHIFT   = 16;
    static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
    static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

    public static void main(String[] args) {
        //System.out.println(EXCLUSIVE_MASK);
        //print(EXCLUSIVE_MASK);
        //print(EXCLUSIVE_MASK + 5);
    }

    public static void print(int num){
        for(int i=31;i>=0;i--){
            System.out.print((num & 1 << i) == 0 ? "0":"1");
        }
    }
}
