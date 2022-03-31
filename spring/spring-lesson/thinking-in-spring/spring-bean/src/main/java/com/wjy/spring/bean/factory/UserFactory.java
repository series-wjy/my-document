package com.wjy.spring.bean.factory;

import com.wjy.spring.ioc.overview.domain.User;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月12日 23:06:00
 */
public interface UserFactory {
    User createUser();

    void initMethod();

    void customDestroy();
}
