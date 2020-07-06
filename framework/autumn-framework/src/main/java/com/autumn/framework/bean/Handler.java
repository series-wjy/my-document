package com.autumn.framework.bean;

import java.lang.reflect.Method;

/**
 * @ClassName Handler.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 封装Action信息
 * @Create 2020年04月25日 09:00:00
 */
public class Handler {
    /**
     * Controller类
     */
    private Class<?> controllerClass;

    /**
     * Action方法
     */
    private Method actionMethod;

    public Handler(Class<?> controllerClass, Method actionMethod) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
}
