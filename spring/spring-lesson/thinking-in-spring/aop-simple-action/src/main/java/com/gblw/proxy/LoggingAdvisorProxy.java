package com.gblw.proxy;

import com.gblw.LoggingUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 输出日志动态代理
 */
public class LoggingAdvisorProxy implements InvocationHandler {

    private Object target;

    private Set<String> methods;

    public LoggingAdvisorProxy(Object target, Set<String> methods) {
        this.target = target;
        this.methods = methods;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if(methods.contains(method.getName())) {
            LoggingUtil.beforeMethod(method.getName());
            result = method.invoke(target, args);
            LoggingUtil.afterMethod(method.getName());
        } else {
            result = method.invoke(target, args);
        }
        return result;
    }
}
