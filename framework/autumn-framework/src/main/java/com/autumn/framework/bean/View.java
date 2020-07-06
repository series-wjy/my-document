package com.autumn.framework.bean;

import java.util.Map;

/**
 * @ClassName View.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 返回视图对象
 * @Create 2020年04月25日 22:41:00
 */
public class View {
    /**
     * 视图路径
     */
    private String path;

    /**
     * 模型数据
     */
    private Map<String, Object> model;

    public View(String path) {
        this.path = path;
    }

    public View addModel(String key, Object value) {
        model.put(key, value);
        return this;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
