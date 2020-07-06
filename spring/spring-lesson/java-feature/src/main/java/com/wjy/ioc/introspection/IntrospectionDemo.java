package com.wjy.ioc.introspection;

import com.wjy.ioc.introspection.domain.UserInfo;
import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * @ClassName IntrospectionDemo.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description java 内省
 * @Create 2020年06月04日 10:09:00
 */
public class IntrospectionDemo {

    public static void main(String[] args) throws Exception{
        UserInfo userInfo = new UserInfo();
        propertyDescriptorOt("name", userInfo);
        introspectorOpt(userInfo);
    }

    private static void introspectorOpt(UserInfo userInfo) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(UserInfo.class);
        Stream.of(beanInfo.getPropertyDescriptors()).forEach(
                (descriptor) -> {
                    Class<?> propertyType = descriptor.getPropertyType();
                    String propertyName = descriptor.getName();
                    if ("age".equals(propertyName)) {
                        descriptor.setPropertyEditorClass(StringToIntegerPropertyEditor.class);
                        PropertyEditor propertyEditor = descriptor.createPropertyEditor(userInfo);
                        propertyEditor.setAsText("11");
                        descriptor.getWriteMethod();
                    }
                }
        );
//        BeanUtils.setProperty(userInfo, "age", "11");
        System.out.println(userInfo);
    }

    private static void propertyDescriptorOt(String name, UserInfo userInfo) throws Exception {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, UserInfo.class);
        Method writeMethod = propertyDescriptor.getWriteMethod();
        writeMethod.invoke(userInfo, "LW");
        System.out.println(userInfo);

        Method readMethod = propertyDescriptor.getReadMethod();
        String userName = (String) readMethod.invoke(userInfo);
        System.out.println(userName);
    }
}
