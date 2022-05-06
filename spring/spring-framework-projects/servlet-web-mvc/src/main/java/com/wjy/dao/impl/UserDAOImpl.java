package com.wjy.dao.impl;

import com.wjy.dao.UserDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月08日 15:46:00
 */
public class UserDAOImpl implements UserDAO {
    @Override
    public List<String> getUsers() {
        return Arrays.asList("lilei", "hanmeimei", "lucy");
    }
}
