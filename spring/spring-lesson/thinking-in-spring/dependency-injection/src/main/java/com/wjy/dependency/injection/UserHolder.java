package com.wjy.dependency.injection;

import com.wjy.spring.ioc.overview.domain.User;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月30日 22:02:00
 */
public class UserHolder {
    private User user;

    public UserHolder() {

    }

    public UserHolder(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserHolder{" +
                "user=" + user +
                '}';
    }
}
