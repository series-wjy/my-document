package com.wjy.jpa.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjy.jpa.model.User;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() throws JsonProcessingException {

        //我们新增7条数据方便测试分页结果
        userRepository.save(User.builder().name("jack1").email("123456@126.com").sex("man").address("shanghai").build());
        userRepository.save(User.builder().name("jack2").email("123456@126.com").sex("man").address("shanghai").build());
        userRepository.save(User.builder().name("jack3").email("123456@126.com").sex("man").address("shanghai").build());
        userRepository.save(User.builder().name("jack4").email("123456@126.com").sex("man").address("shanghai").build());
        userRepository.save(User.builder().name("jack5").email("123456@126.com").sex("man").address("shanghai").build());
        userRepository.save(User.builder().name("jack6").email("123456@126.com").sex("man").address("shanghai").build());
        userRepository.save(User.builder().name("jack7").email("123456@126.com").sex("man").address("shanghai").build());

        //我们利用ObjectMapper将我们的返回结果Json to String
        ObjectMapper objectMapper = new ObjectMapper();

        //返回Stream类型结果（1）
        Stream<User> userStream = userRepository.findAllByCustomQueryAndStream(PageRequest.of(1, 3));
        System.out.println("===========================Stream 第一页数据=================================");
        userStream.forEach(System.out::println);

        //返回分页数据（2）
        Page<User> userPage = userRepository.findAll(PageRequest.of(1, 3));
        System.out.println("===========================Page 第一页数据=================================");
        System.out.println(objectMapper.writeValueAsString(userPage));

        //返回Slice结果（3）
        Slice<User> userSlice = userRepository.findAllByCustomQueryAndSlice(PageRequest.of(1, 3));
        System.out.println("===========================Slice 第一页数据=================================");
        System.out.println(objectMapper.writeValueAsString(userSlice));

        //返回List结果（4）
        List<User> userList = userRepository.findAllById(Lists.newArrayList(1L, 2L));
        System.out.println("===========================List 根据IDS查询=================================");
        System.out.println(objectMapper.writeValueAsString(userList));

    }

}
