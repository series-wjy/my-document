package com.wjy.jpa.controller;

import com.wjy.jpa.model.User;
import com.wjy.jpa.model.relation.UserOneToOne;
import com.wjy.jpa.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 返回值类型示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月04日 15:39:00
 */
@RestController
@RequestMapping("api/rt")
public class ReturnTypeUserController {
    private final UserRepository userRepository;

    public ReturnTypeUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(path = "user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User addUser(@RequestBody User user) {
        User obj = userRepository.save(User.builder().name("jackma").email("123456@126.com").sex("man").address("shanghai").build());
        Assert.notNull(obj, "保存对象失败");

        Streamable<User> userStreamable = userRepository.findAll(PageRequest.of(0,10)).and(User.builder().name("jack222").build());
        userRepository.findAllByCustomQueryAndSlice(PageRequest.of(0, 5));
        userStreamable.forEach(System.out::println);

        return obj;
    }

}
