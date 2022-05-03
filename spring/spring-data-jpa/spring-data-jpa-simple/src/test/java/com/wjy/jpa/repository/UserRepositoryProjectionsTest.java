package com.wjy.jpa.repository;

import com.wjy.jpa.dto.UserOnlyName;
import com.wjy.jpa.model.User;
import com.wjy.jpa.model.UserOnlyNameEmailEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

/**
 * Projections 将 entity 映射成 DTO
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月06日 16:40:00
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryProjectionsTest {

    @Autowired
    private UserOnlyNameEmailEntityRepository userOnlyNameEmailEntityRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testProjections() {
        userRepository.save(User.builder().id(1L).name("jack12").email("123456@126.com").sex("man").address("shanghai").build());
        List<User> users= userRepository.findAll();
        System.out.println(users);
        UserOnlyNameEmailEntity uName = userOnlyNameEmailEntityRepository.getOne(1L);
        System.out.println(uName);

    }

    @Test
    public void testInterfaceDto() {
        UserOnlyName userOnlyName = userRepository.findByAddress("demoData");
        Assertions.assertNotNull(userOnlyName);
        Assertions.assertEquals("demoData", userOnlyName.getName());
    }

}
