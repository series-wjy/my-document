package com.bjhy.common.util;

/**
 * @ClassName StringUtil.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月06日 10:11:46
 */
public class StringUtil {
    /**
     * 判断字符串是否为空.
     *
     * @param val 字符串值
     * @return
     */
    public static boolean isEmpty(String val) {
        return val == null || "".equals(val);
    }

    /**
     * 判断字符串数组是否为空.
     *
     * @param vals 数组对象
     * @return
     */
    public static boolean isEmpty(String[] vals) {
        return vals == null || vals.length == 0;
    }

    /**
     * 判断字符串是否不为空.
     *
     * @param val 字符串值
     * @return
     */
    public static boolean isNotEmpty(String val) {
        return !isEmpty(val);
    }

    /**
     * 判断字符串数组是否不为空.
     *
     * @param vals 数组对象
     * @return
     */
    public static boolean isNotEmpty(String[] vals) {
        return !isEmpty(vals);
    }

    /**
     * 使用间隔符将字符串列表拼接.
     *
     * @param delimiter 间隔符
     * @param strings   字符串列表
     * @return 拼接后的字符串
     */
    public static String join(final char delimiter, final String... strings) {
        if (strings.length == 0) {
            return null;
        }
        if (strings.length == 1) {
            return strings[0];
        }
        final StringBuilder sb = new StringBuilder();
        if (strings[0] != null) {
            sb.append(strings[0]);
        }
        for (int i = 1; i < strings.length; i++) {
            if (!isEmpty(strings[i])) {
                sb.append(delimiter).append(strings[i]);
            } else {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
}
