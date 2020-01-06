package com.bjhy.collect.plugin;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @ClassName Plugin.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年1月2日 16:25:00
 */
public interface Plugin {
    /**
     * 抽象方法：获取className.
     *
     * @return 插件的匹配器
     */
    ElementMatcher.Junction<TypeDescription> buildJunction();

    /**
     * builder转换规则聚合.
     *
     * @param newBuilder      原加强构建器
     * @param typeDescription 类型信息
     * @param classLoader     类加载器
     * @return
     */
    DynamicType.Builder<?> enhance(DynamicType.Builder<?> newBuilder, TypeDescription typeDescription, ClassLoader classLoader);
}