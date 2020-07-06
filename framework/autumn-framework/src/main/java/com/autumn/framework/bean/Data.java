package com.autumn.framework.bean;

/**
 * @ClassName Data.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 返回数据对象
 * @Create 2020年04月25日 22:44:00
 */
public class Data {
    /**
     * 模型数据
     */
    private Object model;

    public Data(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
