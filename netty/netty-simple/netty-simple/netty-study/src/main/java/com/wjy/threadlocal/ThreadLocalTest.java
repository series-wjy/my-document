package com.wjy.threadlocal;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年02月16日 10:46:00
 */
public class ThreadLocalTest {

    public static void main(String[] args) {
        System.out.println(Integer.toBinaryString(64));
        System.out.println(Integer.toBinaryString(63));
        fastThreadLocal();
    }

    public static void fastThreadLocal() {
        FastThreadLocal fastThreadLocal = new FastThreadLocal();
        FastThreadLocal fastThreadLocal2 = new FastThreadLocal();
        FastThreadLocal fastThreadLocal3 = new FastThreadLocal();

        new FastThreadLocalThread(() -> {
            fastThreadLocal.set("xxx");
            fastThreadLocal2.set("xxx");
            fastThreadLocal3.set("xxx");
        }).start();

        new FastThreadLocalThread(() -> {
//            fastThreadLocal.set("yyy");
//            fastThreadLocal.set("ccc");
//            fastThreadLocal.set("ddd");
        }).start();
    }

    private static void threadLocal() {
        ThreadLocal threadLocal = new ThreadLocal<>();

        new Thread(() -> {
           threadLocal.set("xxx");
           threadLocal.set("aaa");
           threadLocal.set("bbb");
        }).start();

        new Thread(() -> {
            threadLocal.set("yyy");
            threadLocal.set("ccc");
            threadLocal.set("ddd");
        }).start();
    }
}
