package com.wjy.jpa.repository;

import com.wjy.jpa.dto.UserDto;
import com.wjy.jpa.dto.UserOnlyName;
import com.wjy.jpa.dto.UserSimpleDto;
import com.wjy.jpa.model.User;
import com.wjy.jpa.model.relation.UserOneToOne;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月09日 14:34:00
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueryAnnotationTest {
    @Autowired
    UserDtoRepository userDtoRepository;

    @Test
    public void testQueryAnnotation() {
        User user = userDtoRepository.findByQuery("demoData");
        System.out.println(user);
    }

    @Test
    public void testJoinQuery() {
        //新增一条用户数据  userDtoRepository.save(User.builder().name("jack").email("123456@126.com").sex("man").address("shanghai").build());
        //再新增一条和用户一对一的UserExtend数据  userExtendRepository.save(UserExtend.builder().userId(1L).idCard("shengfengzhenghao").ages(18).studentNumber("xuehao001").build());
        //查询我们想要的结果
        List<Object[]> userArray = userDtoRepository.findByUserId(22L);
        List<Object[]> userArray1 = userDtoRepository.findByUserId(22L);
        List<Object[]> userArray2 = userDtoRepository.findByUserId(22L);
        System.out.println(String.valueOf(userArray.get(0)[0])+String.valueOf(userArray.get(0)[1]));
        UserDto userDto = UserDto.builder().name(String.valueOf(userArray.get(0)[0])).build();
        System.out.println(userDto);

        UserDto userDto2 = userDtoRepository.findByUserDtoId(22L);
        System.out.println(userDto2);

        UserSimpleDto userDto3 = userDtoRepository.findByUserSimpleDtoId(22L);
        System.out.println(userDto3.getName() + " " + userDto3.getEmail() + " " + userDto3.getIdCard()) ;
    }

    @Test

    public void testQueryDinamicDto() {
        userDtoRepository.save(User.builder().name("jack").email("123456@126.com").sex("man").address("shanghai").build());
        UserOnlyName userDto = userDtoRepository.findByUser("jack", null);
        System.out.println(userDto.getName() + ":" + userDto.getEmail());

        UserOnlyName userDto2 = userDtoRepository.findByUser(User.builder().email("123456@126.com").build());
        System.out.println(userDto2.getName() + ":" + userDto2.getEmail());

    }

}
