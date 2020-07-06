package com.wjy.tis.ioc.overview.domain;

/**
 * @ClassName User.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 用户类
 * @Create 2020年06月02日 20:59:00
 */
public class User {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
