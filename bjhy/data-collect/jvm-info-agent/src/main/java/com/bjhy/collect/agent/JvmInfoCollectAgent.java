package com.bjhy.collect.agent;

import com.bjhy.collect.jvm.JvmCollectAgentInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @ClassName JvmInfoCollectAgent.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月25日 16:25:00
 */
public class JvmInfoCollectAgent {
    /**
     * 在方法在main方法之前执行，和main方法同Jvm、ClassLoader、Security policy和Context
     *
     * @param agentOps javaagent入参
     * @param inst     对class进行字节码加强的代理实例
     */
    public static void premain(String agentOps, Instrumentation inst) {
        System.out.println("hello javaagent!this is premain!");
        // 基于ByteBuddy建立agent规则
        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.ENABLED);
        new AgentBuilder.Default()
                // 忽略不需要拦截的类
                .ignore(initIgnoreElementMatcher())
                // 对类名为Application结尾的的类进行拦截
                .type(ElementMatchers.<TypeDescription>nameEndsWithIgnoreCase("AbstractApplicationContext"))
                // 对拦截类进行加强
                .transform(new AgentTransformer())
                // 类加强的侦听器：类加强过程中的事件侦听
                .with(new AgentListener())
                // 基于inst
                .installOn(inst);
    }

    /**
     * 忽略规则构建
     *
     * @return
     */
    private static ElementMatcher<TypeDescription> initIgnoreElementMatcher() {
        // synthetic是由编译器引入的字段、方法、类或其他结构，主要用于JVM内部使用
        return nameStartsWith("net.bytebuddy.").or(ElementMatchers.<TypeDescription>isSynthetic());
    }

    /**
     * 转换规则构建
     */
    static class AgentTransformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            // 对拦截类的main方法进行拦截加强，加强的规则为JvmDataCollectAgentInterceptor
            return builder.method(ElementMatchers.<MethodDescription>named("refresh"))
                    .intercept(MethodDelegation.withDefaultConfiguration().to(new JvmCollectAgentInterceptor()));
        }
    }

    /**
     * 侦听器
     */
    static class AgentListener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            //System.out.println("onDiscovery:" + typeName);
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
            System.out.println("onTransformation:" + typeDescription);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
            //System.out.println("onIgnored:" + typeDescription);
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            throwable.printStackTrace();
            System.out.println("onError:" + typeName);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            //System.out.println("onComplete:" + typeName);
        }
    }
}
