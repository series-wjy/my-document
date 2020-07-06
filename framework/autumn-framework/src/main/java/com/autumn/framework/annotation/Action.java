package com.autumn.framework.annotation;

/**
 * @ClassName Action.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description Action 方法注解
 * @Create 2020年04月22日 21:49:00
 */
public @interface Action {
    /**
     * 请求类型与路径
     * @return
     */
    String value();
}
