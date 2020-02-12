package com.bjhy.plugins.jvm;
import com.bjhy.core.plugin.api.MonitorMethodInterceptorTemplate;
import com.bjhy.core.plugin.api.Plugin;
import net.bytebuddy.description.method.MethodDescription;
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
public class JvmPlugin implements Plugin {
    public static final String ENHANCE_CLASS = "org.springframework.context.support.AbstractApplicationContext";

    @Override
    public ElementMatcher.Junction<TypeDescription> buildJunction() {
        return ElementMatchers.named(ENHANCE_CLASS);
    }

    @Override
    public DynamicType.Builder<?> enhance(DynamicType.Builder<?> newBuilder, TypeDescription typeDescription, ClassLoader classLoader) {
        return newBuilder.method(ElementMatchers.<MethodDescription>named("refresh"))
                .intercept(MethodDelegation.withDefaultConfiguration().to(MonitorMethodInterceptorTemplate.getTemplate(new JvmCollectInterceptor())));
    }
}
