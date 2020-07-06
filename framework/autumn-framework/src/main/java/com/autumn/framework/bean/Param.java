package com.autumn.framework.bean;

import com.autumn.framework.utils.CastUtil;

import java.util.Map;

/**
 * @ClassName Param.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 请求参数对象
 * @Create 2020年04月25日 22:38:00
 */
public class Param {
    private Map<String, Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 根据参数名，获取Long类型参数
     * @param name
     * @return
     */
    public Long getLong(String name) {
        return CastUtil.castLong(paramMap.get(name));
    }

    /**
     * 获取所有字段信息
     * @return
     */
    public Map<String, Object> getMap() {
        return paramMap;
    }
}
