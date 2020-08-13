package com.wjy.spring.ioc.overview.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName Super.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 超级用户
 * @Create 2020年06月03日 22:21:00
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Super {
}
