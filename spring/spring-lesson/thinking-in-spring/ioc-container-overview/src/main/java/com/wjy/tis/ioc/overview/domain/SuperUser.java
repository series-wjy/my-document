package com.wjy.tis.ioc.overview.domain;

import com.wjy.tis.ioc.overview.annotation.Super;

/**
 * @ClassName SuperUser.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 超级用户
 * @Create 2020年06月03日 22:11:00
 */
@Super
public class SuperUser extends User {
    private String addr;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "SuperUser{" +
                "addr='" + addr + '\'' +
                "} " + super.toString();
    }
}
