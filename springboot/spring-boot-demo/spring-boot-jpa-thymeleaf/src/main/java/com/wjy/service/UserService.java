/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.service;

import com.wjy.entity.User;

import java.util.List;

/**
 * @author wangjiayou 2019/8/7
 * @version ORAS v1.0
 */
public interface UserService {
    List<User> getUserList();

    User findUserById(long id);

    void save(User user);

    void edit(User user);

    void delete(long id);
}
