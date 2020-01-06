package com.bjhy.collect.springmvc;

import com.bjhy.collect.plugin.InstMethodInterceptorTemplate;
import com.bjhy.collect.plugin.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @ClassName ControllerPlugin.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年1月3日 16:00:00
 */
public class ControllerPlugin implements Plugin {
    public static final String ENHANCE_CLASS_ANNOTATION_CONTROLLER = "org.springframework.stereotype.Controller";
    public static final String ENHANCE_CLASS_ANNOTATION_REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    public static final String ENHANCE_METHOD_ANNOTATION = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String ENHANCE_METHOD_ANNOTATION_GET = "org.springframework.web.bind.annotation.PostMapping";
    public static final String ENHANCE_METHOD_ANNOTATION_POST = "org.springframework.web.bind.annotation.GetMapping";
    private static final String INTERCEPTOR_CLASS_NAME = "cn.gy4j.monitor.sniffer.plugins.spring.mvc.annotation.ControllerInterceptor";

    @Override
    public ElementMatcher.Junction<TypeDescription> buildJunction() {
        return ElementMatchers.isAnnotatedWith(ElementMatchers.named(ENHANCE_CLASS_ANNOTATION_REST_CONTROLLER))
                .or(ElementMatchers.isAnnotatedWith(ElementMatchers.named(ENHANCE_CLASS_ANNOTATION_CONTROLLER)))
                .and(ElementMatchers.not(ElementMatchers.isAnnotation()));
    }

    @Override
    public DynamicType.Builder<?> enhance(DynamicType.Builder<?> newBuilder, TypeDescription typeDescription, ClassLoader classLoader) {
        return newBuilder.method(ElementMatchers.isAnnotatedWith(ElementMatchers.named(ENHANCE_METHOD_ANNOTATION))
                .or(ElementMatchers.isAnnotatedWith(ElementMatchers.named(ENHANCE_METHOD_ANNOTATION_GET))).or(ElementMatchers.isAnnotatedWith(ElementMatchers.named(ENHANCE_METHOD_ANNOTATION_POST))))
                .intercept(MethodDelegation.withDefaultConfiguration().to(InstMethodInterceptorTemplate.getTemplate(new ControllerInterceptor())));
    }
}
