package com.wjy.jpa.repository;

import com.wjy.jpa.model.UserInfo;
import com.wjy.jpa.model.relation.UserInfoID;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserInfoRepositoryTest {

   @Autowired
   private UserInfoRepository userInfoRepository;

   @Test
   public void testIdClass() {
      userInfoRepository.save(UserInfo.builder().ages(1).name("jack").telephone("123456789").build());
      Optional<UserInfo> userInfo = userInfoRepository.findById(UserInfoID.builder().name("jack").telephone("123456789").build());

      System.out.println(userInfo.get());
   }

}
