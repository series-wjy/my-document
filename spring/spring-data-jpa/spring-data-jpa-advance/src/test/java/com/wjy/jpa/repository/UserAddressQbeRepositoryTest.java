package com.wjy.jpa.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjy.jpa.domain.SexEnum;
import com.wjy.jpa.domain.UserQbe;
import com.wjy.jpa.domain.UserAddressQbe;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserAddressQbeRepositoryTest {

   @Autowired
   private UserAddressRepository userAddressRepository;
   private Date now = new Date();
   /**
    * 负责添加数据，假设数据库里面已经有的数据
    */
   @BeforeAll
   @Rollback(false)
   @Transactional
   void init() {
      UserQbe user = UserQbe.builder()
            .name("jack")
            .email("123456@126.com")
            .sex(SexEnum.BOY)
            .age(20)
            .createDate(Instant.now())
            .updateDate(now)
            .build();
            userAddressRepository.saveAll(Lists.newArrayList(UserAddressQbe.builder().user(user).address("shanghai").build(),
            UserAddressQbe.builder().user(user).address("beijing").build()));
   }

   @Test
   @Rollback(false)
   public void testQBEFromUserAddress() throws JsonProcessingException {
      UserQbe request = UserQbe.builder()
            .name("jack").age(20).email("12345")
            .build();
      UserAddressQbe address = UserAddressQbe.builder().address("shang").user(request).build();
      ObjectMapper objectMapper = new ObjectMapper();
//    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(address)); //可以打印出来看看参数是什么

//创建匹配器，即如何使用查询条件
      ExampleMatcher exampleMatcher = ExampleMatcher.matching()
            .withMatcher("user.email", ExampleMatcher.GenericPropertyMatchers.startsWith())
            .withMatcher("address", ExampleMatcher.GenericPropertyMatchers.startsWith());
      Page<UserAddressQbe> u = userAddressRepository.findAll(Example.of(address,exampleMatcher), PageRequest.of(0,2));
    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(u));
   }

   @Test
   @Rollback(false)
   public void testQBEExampleMather() throws JsonProcessingException {
      UserQbe request = UserQbe.builder()
              .name("jack").age(20).email("12345")
              .build();
      UserAddressQbe address = UserAddressQbe.builder().address("shang").user(request).build();
      ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
              .withIgnoreCase()
              .withIgnoreNullValues()
              .withIgnorePaths("id", "createDate")
              .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
              .withMatcher("user.email", ExampleMatcher.GenericPropertyMatchers.startsWith())
              .withMatcher("address", ExampleMatcher.GenericPropertyMatchers.startsWith());
      Page<UserAddressQbe> page = userAddressRepository.findAll(Example.of(address, exampleMatcher), PageRequest.of(0,2));
      ObjectMapper objectMapper = new ObjectMapper();
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(page));
   }
}
