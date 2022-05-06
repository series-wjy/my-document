package com.gblw.bean.lifecycle.processor;

import com.gblw.bean.lifecycle.domain.Person;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月18日 20:26:00
 */
public class LifecycleNameReadPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Person) {
            Person person = (Person) bean;
            System.out.println("LifecycleNameReadPostProcessor ------> " + person.getName());
        }
        return bean;
    }
}
