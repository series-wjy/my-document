package com.wjy.jpa.repository;

import com.wjy.jpa.dto.UserDto;
import com.wjy.jpa.dto.UserOnlyName;
import com.wjy.jpa.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月04日 15:37:00
 */
public interface UserRepository extends JpaRepository<User, Long> {

    //根据名称进行查询用户列表
    List<User> findByName(String name);
    // 根据用户的邮箱和名称查询
    List<User> findByEmailAndName(String email, String name);

    UserDto findByIdAndName(String id, String name);

    @Query("select new com.wjy.jpa.dto.UserDto(t.name, t.email, t.idCard) from User t where t.email=:email")
    UserDto findByEmail(@Param("email") String email);

    //自定义一个查询方法，返回Stream对象，并且有分页属性
    @Query("select u from User u")
    Stream<User> findAllByCustomQueryAndStream(Pageable pageable);

    //测试Slice的返回结果
    @Query("select u from User u")
    Slice<User> findAllByCustomQueryAndSlice(Pageable pageable);

    @Async// 一般在 service 层采用异步处理操作，如：发信息、发邮件、发短信等操作
    Future<Slice<UserDto>> findTop1ByName(String name);

    @Async
    CompletableFuture<User> findOneByName(String name);

    @Async
    ListenableFuture<User> findOneByEmail(String name);

    /**
     * 接口的方式返回DTO
     * @param address
     * @return
     */
    UserOnlyName findByAddress(String address);
}
