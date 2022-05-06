package com.wjy.dependency.model;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月23日 16:48:00
 */
@Component
public class Department {

    @Resource(name = "casualWorker")
    private User boss;

    @Inject
    @Named("normalUser")
    private User staff;

    @Override
    public String toString() {
        return "Department{" +
                "boss=" + boss +
                ", staffs=" + staff +
                '}';
    }
}
