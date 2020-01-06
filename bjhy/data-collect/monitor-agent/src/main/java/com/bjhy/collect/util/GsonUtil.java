package com.bjhy.collect.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @ClassName GsonUtil.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月23日 10:12:00
 */
public class GsonUtil {
    private static Gson gson = new GsonBuilder().create();
    /**
     * 对象转json串.
     *
     * @param t   泛型对象
     * @param <T> 泛型类
     * @return json串
     */
    public static <T> String objectToJson(T t) {
        return gson.toJson(t);
    }
}