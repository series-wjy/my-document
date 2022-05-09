package com.gblw.cglib;

import com.gblw.LoggingUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Set;

public class LoggingAdvisorCglibProxy implements MethodInterceptor {
    private Object target;

    private Set<String> methods;

    public LoggingAdvisorCglibProxy(Object target, Set<String> methods) {
        this.target = target;
        this.methods = methods;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        if(methods.contains(method.getName())) {
            LoggingUtil.beforeMethod(method.getName());
            result = method.invoke(target, objects);
            LoggingUtil.afterMethod(method.getName());
        }
        return result;
    }
}
