/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.jpa;

import com.wjy.jpa.dao.MessageRepository;
import com.wjy.jpa.dao2.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wangjiayou 2019/8/1
 * @version ORAS v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MultiDatasourceTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void testMultiDatasource() {
        messageRepository.findAll();
        studentRepository.findAll();
    }

}
