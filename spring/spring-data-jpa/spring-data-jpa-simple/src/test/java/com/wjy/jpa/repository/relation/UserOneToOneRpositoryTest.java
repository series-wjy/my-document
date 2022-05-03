package com.wjy.jpa.repository.relation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wjy.jpa.model.User;
import com.wjy.jpa.model.UserInfo;
import com.wjy.jpa.model.relation.UserInfoOneToOne;
import com.wjy.jpa.model.relation.UserOneToOne;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月01日 23:58:00
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserOneToOneRpositoryTest {

    @Autowired
    private UserInfoOneToOneRepository userInfoOneToOneRepository;
    @Autowired
    private UserOneToOneRepository userOneToOneRepository;

    @BeforeAll
    @Rollback(false)
    @Transactional
    void init() {
        UserOneToOne user = UserOneToOne.builder().name("jackxx").email("123456@126.com").build();
        UserInfoOneToOne userInfo = UserInfoOneToOne.builder().ages(12).userOneToOne(user).telephone("12345678").build();
        userInfoOneToOneRepository.saveAndFlush(userInfo);
    }

    @Test
    @Rollback(false)
    public void testOneToOne() {
        UserOneToOne userOneToOne = UserOneToOne.builder().name("c").sex("man")
                .email("aaa@qq.com").build();
        UserInfoOneToOne userInfoOneToOne = UserInfoOneToOne.builder()
                .userOneToOne(userOneToOne).idCard("123").ages(12).telephone("1234567").build();
        userInfoOneToOneRepository.save(userInfoOneToOne);
        userOneToOneRepository.save(userOneToOne);
    }

    @Test
    @Rollback(false)
    public void testCascadeRemove() {
        UserOneToOne userOneToOne = UserOneToOne.builder().name("c").sex("man")
                .email("aaa@qq.com").build();
//        UserInfoOneToOne userInfoOneToOne = UserInfoOneToOne.builder()
//                .userOneToOne(userOneToOne).idCard("123").ages(12).telephone("1234567").build();
//        userInfoOneToOneRepository.saveAndFlush(userInfoOneToOne);
//        userInfoOneToOne.setUserOneToOne(null);
//        userInfoOneToOneRepository.delete(userInfoOneToOne);
    }

    @Test
    @Rollback(false)
    public void testOrphanRemoval() {
        UserOneToOne userOneToOne = UserOneToOne.builder().name("c").sex("man")
                .email("aaa@qq.com").build();
//        UserInfoOneToOne userInfoOneToOne = UserInfoOneToOne.builder()
//                .userOneToOne(userOneToOne).idCard("123").ages(12).telephone("1234567").build();
//        userInfoOneToOneRepository.saveAndFlush(userInfoOneToOne);
//        userInfoOneToOne.setUserOneToOne(null);
//        userInfoOneToOneRepository.delete(userInfoOneToOne);
    }

    /**
     * 测试延迟加载
     *
     * @throws JsonProcessingException
     */
    @Test
    @Rollback(false)
    public void testRelationShipLazyLoad() throws JsonProcessingException {
        UserInfoOneToOne userInfo1 = userInfoOneToOneRepository.getOne(145L);
        UserOneToOne userOneToOne = userOneToOneRepository.getOne(146L);
        System.out.println(userOneToOne);
        System.out.println(userInfo1);
//        System.out.println(userInfo1.getUserOneToOneId());
    }
}
