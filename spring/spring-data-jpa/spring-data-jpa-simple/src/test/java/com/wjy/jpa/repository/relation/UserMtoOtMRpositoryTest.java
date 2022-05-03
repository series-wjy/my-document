package com.wjy.jpa.repository.relation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wjy.jpa.model.User;
import com.wjy.jpa.model.UserAddress;
import com.wjy.jpa.model.relation.UserInfoJoinColumn;
import com.wjy.jpa.model.relation.UserJoinColumn;
import com.wjy.jpa.repository.UserAddressRepository;
import com.wjy.jpa.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * 测试 {@link ManyToOne} 和 {@link OneToMany} 的用法
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月01日 23:58:00
 */
@DataJpaTest
@Rollback(false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserMtoOtMRpositoryTest {

    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private UserRepository userRepository;
    /**
     * 负责添加数据
     */
    @BeforeAll
    @Transactional
    void init() {
        User user = User.builder().name("jackxx").email("123456@126.com").build();
        UserAddress userAddress = UserAddress.builder().address("shanghai1").user(user).build();
        UserAddress userAddress2 = UserAddress.builder().address("shanghai2").user(user).build();
        userAddressRepository.saveAll(Lists.newArrayList(userAddress,userAddress2));
    }

    /**
     * 测试用User关联关系操作
     * @throws JsonProcessingException
     */
    @Test
    @Rollback(false)
    public void test() throws JsonProcessingException {
        User user = userRepository.getOne(120L);
        System.out.println(user.getName());
        System.out.println(user.getUserAddresses());
    }
}
