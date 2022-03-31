package com.wjy.spring.bean.factory;

import com.wjy.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.FactoryBean;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月14日 16:07:00
 */
public class UserFactoryBean implements FactoryBean<User> {
    @Override
    public User getObject() throws Exception {
        User user = new User();
        user.setId(11L);
        user.setName("FactoryBean版隔壁老王");
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }
}
