package com.bjhy.collect.plugin;

import com.bjhy.collect.interceptor.MonitorInterceptor;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * author   gy4j
 * Email    76429197@qq.com
 * Date     2019-08-27
 */
public class InstMethodInterceptorTemplate {
    /**
     * 实例方法拦截实现对象.
     */
    private MonitorInterceptor interceptor;

    /**
     * TODO
     * @Param interceptor
     * @Return : com.bjhy.collect.plugin.InstMethodInterceptorTemplate
     * @Create: 2020/1/6 10:19
     * @Author: wangjiayou
     */
    public static InstMethodInterceptorTemplate getTemplate(MonitorInterceptor interceptor) {
        return new InstMethodInterceptorTemplate(interceptor);
    }

    private InstMethodInterceptorTemplate(MonitorInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * 基于ByteBuddy的拦截方法.
     *
     * @param obj          原对象
     * @param allArguments 参数
     * @param zuper        原调用
     * @param method       方法
     * @return
     */
    @RuntimeType
    public Object intercept(@This Object obj,
                            @AllArguments Object[] allArguments,
                            @SuperCall Callable<?> zuper,
                            @Origin Method method) throws Throwable {
        Object span = null;
        try {
            span = interceptor.beforeMethod(method, allArguments);
        } catch (Throwable t) {
        }

        Object ret = null;

        try {
            ret = zuper.call();
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(method, allArguments, t, span);
            } catch (Throwable t2) {
            }
            throw t;
        } finally {
            try {
                System.out.println("================执行finally=====================" + interceptor.toString());
                ret = interceptor.afterMethod(method, allArguments, ret, span);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return ret;
    }
}