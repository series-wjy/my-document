package com.wjy.jpa.controller;

import com.wjy.jpa.dto.UserDto;
import com.wjy.jpa.model.User;
import com.wjy.jpa.model.relation.UserOneToOne;
import com.wjy.jpa.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 异步查询示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月04日 15:39:00
 */
@RestController
@RequestMapping("api/async")
public class AsyncUserController {
    private final UserRepository userRepository;

    public AsyncUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(path = "user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Slice<UserDto> addUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        Future<Slice<UserDto>> future = userRepository.findTop1ByName("demoData");
        Assert.notNull(future, "保存对象失败");

        Streamable<User> userStreamable = userRepository.findAll(PageRequest.of(0,10)).and(User.builder().name("jack222").build());

        userStreamable.forEach(System.out::println);

        return future.get();
    }

}
