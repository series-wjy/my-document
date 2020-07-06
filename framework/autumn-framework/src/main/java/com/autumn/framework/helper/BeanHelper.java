package com.autumn.framework.helper;

import com.autumn.framework.utils.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName BeanHelper.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description Bean 助手类
 * @Create 2020年04月22日 22:16:00
 */
public final class BeanHelper {
    /**
     * 定义bean映射关系
     */
    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<>();

    static {
        Set<Class<?>> classSet = ClassHelper.getBeanClassSet();
        classSet.forEach((clazz) -> {
            BEAN_MAP.put(clazz, ReflectionUtil.newInstance(clazz));
        });
    }

    /**
     * 获取bean映射
     * @return
     */
    public static Map<Class<?>, Object> getBeanMap() {
        return BEAN_MAP;
    }

    /**
     * 获取Bean实例
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        if(!BEAN_MAP.containsKey(clazz)) {
            throw new RuntimeException("can not get bean by Class: " + clazz);
        }
        return (T) BEAN_MAP.get(clazz);
    }
}
