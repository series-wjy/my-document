package com.gblw.bf;

import com.gblw.dao.DemoDao;
import com.gblw.dao.DemoOracleDao;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

public class BeanFactory {
    private static Properties properties;

    private static Map<String, Object> cache = new HashMap<>();

    static {
        properties = new Properties();
        try {
            InputStream resourceAsStream = BeanFactory.class.getResourceAsStream("/factory.properties");
            properties.load(BeanFactory.class.getClassLoader().getResourceAsStream("factory.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object getBean(String beanName) {
        if(cache.get(beanName) != null) {
            return cache.get(beanName);
        }
        try {
            String className = properties.getProperty(beanName);
            String advisor = properties.getProperty(beanName + ".advisor");
            String methods = properties.getProperty(beanName + ".advisor.methods");
            Object target = Class.forName(className).newInstance();
            Object bean = target;
            if(advisor != null && !"".equals(advisor)) {
                Set<String> methodsSet = new HashSet<>();
                if(methods != null && !"".equals(methods)) {
                    methodsSet.addAll(Arrays.asList(methods.split(",")));
                }
                Class<?> handlerClass = Class.forName(advisor);
                Object handler = handlerClass.getConstructors()[0].newInstance(target, methodsSet);
                Object proxyInstance = Proxy.newProxyInstance(bean.getClass().getClassLoader(),
                        bean.getClass().getInterfaces(), (InvocationHandler) handler);
                bean = proxyInstance;
            }
            cache.put(beanName, bean);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bean instantiation error, cause: " + e.getMessage());
        }
    }
}