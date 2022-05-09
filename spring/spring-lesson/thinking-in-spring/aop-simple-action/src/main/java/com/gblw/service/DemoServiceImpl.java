package com.gblw.service;

import com.gblw.bf.BeanFactory;
import com.gblw.dao.DemoDao;

import java.io.InputStream;
import java.util.List;

public class DemoServiceImpl implements DemoService {

    private DemoDao demoDao = (DemoDao) BeanFactory.getBean("demoDao");
    
    @Override
    public List<String> findAll() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/factory.properties");
        return demoDao.findAll();
    }

    @Override
    public int add(String userId, int points) {
        System.out.println("DemoServiceImpl add ...");
        System.out.println("user: " + userId + ", points: " + points);
        return points;
    }

    @Override
    public int subtract(String userId, int points) {
        System.out.println("DemoServiceImpl subtract ...");
        System.out.println("user: " + userId + ", points: " + points);
        return points;
    }
}