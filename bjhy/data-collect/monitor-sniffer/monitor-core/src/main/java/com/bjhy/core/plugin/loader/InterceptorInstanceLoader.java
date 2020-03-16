package com.bjhy.core.plugin.loader;

import com.bjhy.core.loader.AgentClassLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName InterceptorInstanceLoader.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年2月7日 15:11:23
 */
public class InterceptorInstanceLoader {
    /**
     * 缓存interceptor实例对象.
     */
    private static ConcurrentHashMap<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<String, Object>();
    /**
     * 缓存加载器实例.
     */
    private static Map<ClassLoader, ClassLoader> EXTEND_PLUGIN_CLASSLOADERS = new HashMap<ClassLoader, ClassLoader>();
    /**
     * 类加载加锁.
     */
    private static ReentrantLock INSTANCE_LOAD_LOCK = new ReentrantLock();

    /**
     * 获取interceptor实例对象.
     *
     * @param className         类名
     * @param targetClassLoader 类加载器
     * @return
     */
    public static <T> T load(String className, ClassLoader targetClassLoader)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (targetClassLoader == null) {
            targetClassLoader = InterceptorInstanceLoader.class.getClassLoader();
        }
        String instanceKey = className + "_OF_" + targetClassLoader.getClass().getName() + "@" + Integer.toHexString(targetClassLoader.hashCode());
        Object inst = INSTANCE_CACHE.get(instanceKey);
        if (inst == null) {
            INSTANCE_LOAD_LOCK.lock();
            try {
                ClassLoader pluginLoader = EXTEND_PLUGIN_CLASSLOADERS.get(targetClassLoader);
                if (pluginLoader == null) {
                    pluginLoader = new AgentClassLoader(targetClassLoader);
                    EXTEND_PLUGIN_CLASSLOADERS.put(targetClassLoader, pluginLoader);
                }
                inst = Class.forName(className, true, pluginLoader).newInstance();
            } finally {
                INSTANCE_LOAD_LOCK.unlock();
            }
            if (inst != null) {
                INSTANCE_CACHE.put(instanceKey, inst);
            }
        }

        return (T) inst;
    }
}
