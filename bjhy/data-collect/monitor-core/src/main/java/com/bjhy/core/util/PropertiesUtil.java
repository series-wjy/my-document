package com.bjhy.core.util;

import java.util.Properties;

/**
 * @ClassName PropertiesUtil.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月06日 16:22:17
 */
public class PropertiesUtil {
    private static final String PERFIX = "${";
    private static final String SUFFIX = "}";
    private static final String SPLIT = "::";

    /**
     * 解析properties的value
     * 格式：${XXX:default}
     * 根据XXX到环境变量和JVM变量里面去尝试获取value，如果获取不到返回default.
     *
     * @param key           key值
     * @param properties    properties对象
     * @return
     */
    public static String parseValue(String key, Properties properties) {
        String value = properties.getProperty(key).trim();
        return parseValue(value);
    }

    /**
     * 解析value.
     *
     * @param value value值
     * @return
     */
    private static final String parseValue(String value) {
        if (StringUtil.isEmpty(value)) {
            return value;
        }

        if (!value.startsWith(PERFIX)) {
            return value;
        }

        String content = value.substring(value.indexOf(PERFIX) + 2, value.lastIndexOf(SUFFIX));
        String[] cs = content.split(SPLIT);
        if (cs.length > 2) {
            throw new RuntimeException("不能识别的配置：" + value);
        }
        String placeHolder = cs[0].trim();
        String defaultValue = null;
        if (cs.length == 2) {
            defaultValue = cs[1].trim();
        }
        return getConfigValue(placeHolder, defaultValue);
    }

    /**
     * 获取配置的value.
     *
     * @param placeHolder   配置名
     * @param defaultValue  默认值
     * @return
     */
    private static String getConfigValue(String placeHolder, String defaultValue) {
        String value = System.getProperty(placeHolder);
        if (StringUtil.isEmpty(value)) {
            value = System.getenv(placeHolder);
        }
        if (StringUtil.isEmpty(value)) {
            value = defaultValue;
        }
        if (StringUtil.isEmpty(value)) {
            return "";
        }
        return value;
    }
}
