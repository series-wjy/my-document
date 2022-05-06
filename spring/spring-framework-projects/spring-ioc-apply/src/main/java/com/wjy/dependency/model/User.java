package com.wjy.dependency.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月08日 17:43:00
 */
@Component("normalUser")
@Getter
@Setter
public class User {
    @Value("normalUser")
    private String name;
}
