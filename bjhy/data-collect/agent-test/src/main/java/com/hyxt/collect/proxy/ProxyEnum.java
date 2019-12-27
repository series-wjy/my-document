package com.hyxt.collect.proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import sun.reflect.misc.ReflectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 代理技术枚举类
 */
public enum ProxyEnum {
    JDK_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) {
            return inferfaceClass.cast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{inferfaceClass}, (InvocationHandler) handler));
        }
    }),
    BYTE_BUDDY_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) throws IllegalAccessException, InstantiationException {
            Class<? extends T> cls = new ByteBuddy()
                    .subclass(inferfaceClass)
                    .method(ElementMatchers.isDeclaredBy(inferfaceClass))
                    .intercept(MethodDelegation.to(handler, "handler"))
                    .make()
                    .load(inferfaceClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return (T) ReflectUtil.newInstance(cls);
        }
    }),
    CGLIB_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) {
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback((MethodInterceptor) handler);
            enhancer.setInterfaces(new Class[]{inferfaceClass});
            return (T) enhancer.create();
        }
    }),
//    JAVASSIST_BYTECODE_PROXY(new ProxyFactory() {
//        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) {
//            return (T) Proxy.getProxyClass(this.getClass().getClassLoader(), inferfaceClass).newInstance(handler);
//        }s
//    }),
    JAVASSIST_DYNAMIC_PROXY(new ProxyFactory() {
        public <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) throws IllegalAccessException, InstantiationException {
            javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
            proxyFactory.setInterfaces(new Class[]{inferfaceClass});
            Class<?> proxyClass = proxyFactory.createClass();
            T javassistProxy = (T) ReflectUtil.newInstance(proxyClass);
            ((ProxyObject) javassistProxy).setHandler((MethodHandler) handler);
            return javassistProxy;
        }
    });
    private ProxyFactory factory;

    ProxyEnum(ProxyFactory factory) {
        this.factory = factory;
    }

    public <T> T newProxyInstance(Class<T> interfaceType, Object handler) throws InstantiationException, IllegalAccessException {
        return factory.newProxyInstance(interfaceType, handler);
    }

    interface ProxyFactory {
        <T> T newProxyInstance(Class<T> inferfaceClass, Object handler) throws IllegalAccessException, InstantiationException;
    }
}  