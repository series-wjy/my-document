/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy;

import com.wjy.dao.DeptDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangjiayou 2019/6/24
 * @version ORAS v1.0
 */

public class TestMain {

    private SqlSession session;

    @Before
    public void start() throws IOException {
        InputStream is = Resources.getResourceAsStream("myBatis-config.xml");
        SqlSessionFactoryBuilder builderObj = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builderObj.build(is);
        session = factory.openSession();
    }

    @After
    public void close() {
        if(session != null) {
            session.close();
        }
    }

    @Test
    public void test() {
        DeptDao dao  = session.getMapper(DeptDao.class);
        //session.insert("");
        dao.findByDeptNo(1);
    }
}
