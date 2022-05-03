package com.gblw.pp;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月11日 09:14:00
 */
public class CarInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if(beanName.equals("car")) {
            Car car = new Car();
            car.setName("法拉利");
            return car;
        }
        return null;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if(beanName.equals("car2")) {
            MutablePropertyValues mpv = new MutablePropertyValues(pvs);
            mpv.addPropertyValue("name", "劳斯莱斯");
            return mpv;
        }
        return null;
    }
}
