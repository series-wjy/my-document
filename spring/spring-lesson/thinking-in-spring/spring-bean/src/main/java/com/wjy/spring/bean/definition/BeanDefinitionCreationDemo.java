package com.wjy.spring.bean.definition;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * @ClassName BeanDefinitionCreationDemo.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description {@link org.springframework.beans.factory.config.BeanDefinition} 构建示例
 * @Create 2020年08月12日 22:34:00
 */
public class BeanDefinitionCreationDemo {
    public static void main(String[] args) {
        // 1、通过 BeanDefinitionBuilder 构建
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        builder.addPropertyValue("id", 1);
        builder.addPropertyValue("name", "隔壁老王");
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        // BeanDefinition 并非 Bean 的终态，可以自定义修改

        // 2、通过 AbstractBeanDefinition 的派生类构建
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        // 设置 Bean 类型
        genericBeanDefinition.setBeanClass(User.class);
        // 设置 Bean 属性
        MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        mutablePropertyValues.add("id", 2)
                .addPropertyValue("name", "隔壁小王");
//        mutablePropertyValues.addPropertyValue("id", 2);
//        mutablePropertyValues.addPropertyValue("name", "隔壁小王");
        genericBeanDefinition.setPropertyValues(mutablePropertyValues);
    }
}
