package com.autumn.framework;

import com.autumn.framework.helper.BeanHelper;
import com.autumn.framework.helper.ClassHelper;
import com.autumn.framework.helper.ControllerHelper;
import com.autumn.framework.utils.ClassUtil;
import com.autumn.framework.utils.IocHelper;

/**
 * @ClassName HelperLoader.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 加载相应的Helper类
 * @Create 2020年04月25日 21:31:00
 */
public final class HelperLoader {
    public static void init() {
        Class<?>[] helpers = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for(Class<?> clazz : helpers) {
            ClassUtil.loadClass(clazz.getName());
        }
    }
}
