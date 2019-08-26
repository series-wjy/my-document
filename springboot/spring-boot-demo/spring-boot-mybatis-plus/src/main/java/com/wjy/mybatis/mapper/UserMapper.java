package com.wjy.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.mybatis.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}