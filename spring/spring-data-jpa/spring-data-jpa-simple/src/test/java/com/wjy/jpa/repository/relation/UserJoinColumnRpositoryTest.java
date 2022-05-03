package com.wjy.jpa.repository.relation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.wjy.jpa.model.relation.UserInfoJoinColumn;
import com.wjy.jpa.model.relation.UserInfoOneToOne;
import com.wjy.jpa.model.relation.UserJoinColumn;
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
@Rollback(false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserJoinColumnRpositoryTest {

    @Autowired
    private UserInfoJoinColumnRepository userInfoJoinColumnRepository;
    @Autowired
    private UserJoinColumnRepository userJoinColumnRepository;

    @BeforeAll
    @Transactional
    void init() {
        UserJoinColumn user = UserJoinColumn.builder().name("jackxx").email("123456@126.com").build();
        UserInfoJoinColumn userInfo = UserInfoJoinColumn.builder().ages(12).userJoinColumn(user).telephone("12345678").build();
        userInfoJoinColumnRepository.saveAndFlush(userInfo);
    }

    @Test
    public void testUserJoinColumn() {
        UserJoinColumn userJoinColumn = UserJoinColumn.builder().name("c").sex("man")
                .email("aaa@qq.com").build();
        UserInfoJoinColumn userInfoJoinColumn = UserInfoJoinColumn.builder()
                .userJoinColumn(userJoinColumn).idCard("123").ages(12).telephone("1234567").build();
        userInfoJoinColumnRepository.save(userInfoJoinColumn);
    }

    @Test
    public void testJsonLazy() throws JsonProcessingException {
        UserInfoJoinColumn one = userInfoJoinColumnRepository.getOne(138L);
        UserJoinColumn two = userJoinColumnRepository.getOne(137L);
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new Hibernate5Module().enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING));
        //直接关闭SerializationFeature.FAIL_ON_EMPTY_BEANS，解决延迟加载时报错问题
        //mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(one);
        String s1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(two);
        System.out.println(s);
        System.out.println(s1);
        System.out.println(one);
    }
}
