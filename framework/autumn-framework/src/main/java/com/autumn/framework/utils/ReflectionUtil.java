package com.autumn.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName RefectionUtil.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 反射工具类
 * @Create 2020年04月22日 22:04:00
 */
public final class ReflectionUtil {
    private final static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 创建实例
     * @param clazz
     * @return
     */
    public static Object newInstance(Class<?> clazz) {
        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("new instance failure", e);
            throw new RuntimeException(e);
        }
        return instance;
    }

    /**
     * 调用方法
     * @param method
     * @param obj
     * @param args
     * @return
     */
    public static Object invokeMethod(Method method, Object obj, Object...args) {
        Object result;
        try {
            result = method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("invoke method failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 设置字段的值
     * @param field
     * @param obj
     * @param value
     */
    public static void setField(Field field, Object obj, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("set field value failure", e);
            throw new RuntimeException(e);
        }
    }
}
