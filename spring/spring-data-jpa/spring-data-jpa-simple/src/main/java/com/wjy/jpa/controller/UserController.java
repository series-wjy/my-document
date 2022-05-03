package com.wjy.jpa.controller;

import com.wjy.jpa.dto.UserDto;
import com.wjy.jpa.model.User;
import com.wjy.jpa.model.relation.UserOneToOne;
import com.wjy.jpa.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月04日 15:39:00
 */
@RestController
@RequestMapping("api/v1")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(path = "user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User addUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{name}")
    public List<User> findByName(@PathVariable("name") String name) {
        return userRepository.findByName(name);
    }

    @GetMapping("/{id}/{name}")
    public UserDto findById(@PathVariable("id") String id, @PathVariable("name") String name) {
        return userRepository.findByIdAndName(id, name);
    }

    @GetMapping("/email/{email}")
    public UserDto findByEmail(@PathVariable("email") String email) {
        return userRepository.findByEmail(email);
    }

    @GetMapping("users")
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 验证排序和分页查询方法，Pageable的默认实现类：PageRequest
     * @return
     */
    @GetMapping(path = "/page")
    @ResponseBody
    public Page<User> getAllUserByPage() {
        return userRepository.findAll(
                PageRequest.of(1, 20, Sort.by(new Sort.Order(Sort.Direction.ASC,"name"))));
    }
    /**
     * 排序查询方法，使用Sort对象
     * @return
     */
    @GetMapping(path = "/sort")
    @ResponseBody
    public Iterable<User> getAllUsersWithSort() {
        return userRepository.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC,"name")));
    }
}
