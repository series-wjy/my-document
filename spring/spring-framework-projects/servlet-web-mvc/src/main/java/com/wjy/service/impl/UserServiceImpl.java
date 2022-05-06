package com.wjy.service.impl;

import com.wjy.dao.UserDAO;
import com.wjy.ioc.BeanFactory;
import com.wjy.service.UserService;

import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月08日 15:46:00
 */
public class UserServiceImpl implements UserService {

    private UserDAO userDAO = (UserDAO) BeanFactory.getBean("userDaoImpl");

    @Override
    public List<String> getUsers() {
        return userDAO.getUsers();
    }
}
