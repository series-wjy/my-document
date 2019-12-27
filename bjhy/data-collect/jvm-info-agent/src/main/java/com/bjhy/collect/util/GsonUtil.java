package com.bjhy.collect.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
     * json串传map.
     *
     * @param jsonStr json串
     * @return map对象
     */
    public static Map<String, Object> jsonToMap(String jsonStr) {
        Map<String, Object> map = gson.fromJson(jsonStr, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        return map;
    }

    /**
     * json串转对象.
     *
     * @param jsonStr json串
     * @param clazz   对象类型
     * @param <T>     泛型类
     * @return 泛型对象
     */
    public static <T> T jsonToObject(String jsonStr, Class<T> clazz) {
        return gson.fromJson(jsonStr, clazz);
    }

    /**
     * json串转Json对象.
     *
     * @param jsonStr json串
     * @return jsonElement
     */
    public static JsonElement jsonToObject(String jsonStr) {
        JsonElement returnData = JsonParser.parseString(jsonStr);
        return returnData;
    }

    /**
     * map转对象.
     *
     * @param map   map对象
     * @param clazz 对象类型
     * @param <T>   泛型类
     * @return 泛型对象
     */

    public static <T> T mapToObject(Map map, Class<T> clazz) {
        String json = gson.toJson(map);
        return jsonToObject(json, clazz);
    }

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

    /**
     * json串转列表.
     *
     * @param json json串
     * @param type 类型
     * @return 对象列表
     */
    public static <T> List<T> jsonToList(String json, Type type) {
        List<T> list = gson.fromJson(json, type);
        return list;
    }
}