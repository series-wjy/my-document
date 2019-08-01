/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.jpa;

import com.wjy.jpa.dao.UserJpaRepository;
import com.wjy.jpa.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wangjiayou 2019/8/1
 * @version ORAS v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaTest {

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    public void testBaseQuery() throws Exception {
        User user=new User();
        userRepository.findAll();
        user.setId(1L);
        userRepository.findOne(Example.of(user));
        userRepository.save(user);
        userRepository.delete(user);
        userRepository.count();
        userRepository.exists(Example.of(user));
        // ...
    }

    @Test
    public void testPageQuery() throws Exception {
        int page=1,size=10;
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        userRepository.findAll(pageable);
        userRepository.findByUserName("testName", pageable);

        userRepository.deleteByUserId(1L);
    }
}
