package com.autumn.framework.helper;

import com.autumn.framework.annotation.Controller;
import com.autumn.framework.annotation.Service;
import com.autumn.framework.utils.ClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName ClassHelper.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 类操作助手
 * @Create 2020年04月22日 21:52:00
 */
public final class ClassHelper {
    /**
     * 存放所有加载类的集合
     */
    private static final Set<Class<?>> CLASS_SET;
    static {
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    /**
     * 获取应用包下所有类的集合
     * @return
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    /**
     * 获取应用包下所有Service的集合
     * @return
     */
    public static Set<Class<?>> getServiceClassSet() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        CLASS_SET.forEach((clazz) -> {
            if(clazz.isAnnotationPresent(Service.class)) {
                set.add(clazz);
            }
        });
        return set;
    }

    /**
     * 获取应用包下所有Controller的集合
     * @return
     */
    public static Set<Class<?>> getControllerClassSet() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        CLASS_SET.forEach((clazz) -> {
            if(clazz.isAnnotationPresent(Controller.class)) {
                set.add(clazz);
            }
        });
        return set;
    }

    /**
     * 获取应用包下所有Bean的集合（包括Service、Controller）
     * @return
     */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.addAll(getControllerClassSet());
        set.addAll(getServiceClassSet());
        return set;
    }
}
