package com.wjy.ioc;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月08日 15:54:00
 */
public class BeanFactory {

    private static BeanFactory beanFactory = new BeanFactory();

    private static Properties properties;
    private static ConcurrentHashMap<String, Object> bean_cache = new ConcurrentHashMap<>();
    static {
        properties = new Properties();
        try {
            properties.load(BeanFactory.class.getClassLoader().getResourceAsStream("factory.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * 根据名称获取 bean
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        if (bean_cache.containsKey(name)) {
            return bean_cache.get(name);
        } else {
            synchronized (bean_cache) {
                try {
                    Class clazz = Class.forName(properties.getProperty(name));
                    Object bean = clazz.newInstance();
                    bean_cache.put(name, bean);
                    return bean;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
