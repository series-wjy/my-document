package com.autumn.framework.utils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @ClassName IocHelper.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 数组工具类
 * @Create 2020年04月22日 23:10:00
 */
public final class ArrayUtil {

    /**
     * 判断数组是否非空
     */
    public static boolean isNotEmpty(Object[] array) {
        return !ArrayUtils.isEmpty(array);
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return ArrayUtils.isEmpty(array);
    }
}
