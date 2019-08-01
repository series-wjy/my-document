package com.wjy.redis.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String userName;
    private String passWord;
    private String email;
    private String nickName;

    public User(String userName, String passWord, String email, String nickName, String regTime) {
        this.userName = userName;
        this.passWord = passWord;
        this.email = email;
        this.nickName = nickName;
        this.regTime = regTime;
    }

    private String regTime;

    public User() {

    }
    //省略getter settet方法、构造方法

}