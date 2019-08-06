package com.wjy.shiro.service.impl;

import com.wjy.shiro.dao.UserInfoDao;
import com.wjy.shiro.entity.UserInfo;
import com.wjy.shiro.service.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;

    @Override
    public UserInfo findByUsername(String username) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        UserInfo userInfo = userInfoDao.findByUsername(username);
        return userInfo;
    }
}