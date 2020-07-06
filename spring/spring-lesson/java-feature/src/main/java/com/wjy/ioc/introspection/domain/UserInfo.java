package com.wjy.ioc.introspection.domain;

/**
 * @ClassName UserInfo.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 用户类
 * @Create 2020年06月04日 10:09:00
 */
public class UserInfo {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
