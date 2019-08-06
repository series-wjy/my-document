package com.wjy.shiro.service;

import com.wjy.shiro.entity.UserInfo;

public interface UserInfoService {
    /**通过username查找用户信息;*/
    UserInfo findByUsername(String username);
}