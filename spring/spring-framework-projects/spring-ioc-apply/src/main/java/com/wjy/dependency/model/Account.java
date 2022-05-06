package com.wjy.dependency.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月11日 17:10:00
 */
@Component
@Getter
@Setter
@ToString
public class Account {

    @Autowired
    private User casualWorker;

    @Autowired
    private List<User> users;
}
