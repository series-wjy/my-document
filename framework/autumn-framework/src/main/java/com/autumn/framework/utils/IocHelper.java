package com.autumn.framework.utils;

import com.autumn.framework.annotation.Inject;
import com.autumn.framework.helper.BeanHelper;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @ClassName IocHelper.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 依赖注入助手类
 * @Create 2020年04月22日 23:10:00
 */
public final class IocHelper {
    static {
        // 获取所有Bean类与Bean实例之间的映射关系
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if(CollectionUtil.isNotEmpty(beanMap)) {
            beanMap.entrySet().forEach((key) -> {
                // 获取Bean类的实例
                Class<?> clazz = key.getKey();
                Object beanInstance = beanMap.get(clazz);
                // 获取Bean类的所有成员变量
                Field[] fields = clazz.getDeclaredFields();
                if(ArrayUtil.isNotEmpty(fields)) {
                    for(Field field : fields) {
                        // 判断当前Bean Field是否带有Inject注解
                        if(field.isAnnotationPresent(Inject.class)) {
                            // 在Bean Map中查找成员变量对应的实例
                            Class<?> beanFieldClass = field.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if(beanFieldInstance != null) {
                                ReflectionUtil.setField(field, beanInstance, beanFieldInstance);
                            }
                        }
                    }
                }
            });
        }
    }
}
