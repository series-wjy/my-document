package com.autumn.framework.helper;

import com.autumn.framework.annotation.Action;
import com.autumn.framework.bean.Handler;
import com.autumn.framework.bean.Request;
import com.autumn.framework.utils.ArrayUtil;
import com.autumn.framework.utils.CollectionUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName ControllerHelper.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 控制器助手类
 * @Create 2020年04月25日 09:03:00
 */
public final class ControllerHelper {
    /**
     * 用于存放请求与处理器的映射关系
     */
    private final static Map<Request, Handler> ACTION_MAP= new HashMap<>();

    static {
        // 获取所有Controller类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if(CollectionUtil.isNotEmpty(controllerClassSet)) {
            controllerClassSet.forEach((controllerClass) -> {
                // 获取Controller类中定义的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if(ArrayUtil.isNotEmpty(methods)) {
                    Arrays.stream(methods).forEach(method -> {
                        // 判断Method是否带有Action注解
                        if(method.isAnnotationPresent(Action.class)) {
                            // 从Action中获取URL映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            // 验证URL映射规则
                            if(mapping.matches("\\w+:/\\w*")) {
                                String[] array = mapping.split(":");
                                if(ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    // 获取请求方法与请求路径
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod, requestPath);

                                    Handler handler = new Handler(controllerClass, method);
                                    // 初始化Action Map
                                    ACTION_MAP.put(request, handler);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取Handler
     * @param requestMethod
     * @param requestPath
     * @return
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
