package com.wjy.jpa.repository;

import com.wjy.jpa.dto.UserDto;
import com.wjy.jpa.dto.UserOnlyName;
import com.wjy.jpa.dto.UserSimpleDto;
import com.wjy.jpa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月04日 15:37:00
 */
public interface UserDtoRepository extends JpaRepository<User, Long> {

    //通过query注解根据name查询user信息
    @Query("From User where name=:name")
    User findByQuery(@Param("name") String name);

//    @Query(value = "select * from user where name=?1",nativeQuery = true)
//    List<User> findByFirstName(String firstName, Sort sort);

    // nativeQuery=true 排序的正确写法
    @Query(value = "select * from user where name=?1 order by ?2",nativeQuery = true)
    List<User> findByFirstName(String firstName, String sort);


    // nativeQuery=true 分页的特殊写法
    @Query(value = "select * from user where name=?1 /* #pageable# */",
            countQuery = "select count(*) from user where name=?1",
            nativeQuery = true)
    Page<User> findByFirstName(String name, Pageable pageable);

    /**
     * 查询用户表里面的name、email和UserExtend表里面的idCard
     * @param id
     * @return
     */
    @Query("select u.name,u.email,e.idCard from User u,UserExtend e where u.id= e.userId and u.id=:id")
    List<Object[]> findByUserId(@Param("id") Long id);

    @Query("select new com.wjy.jpa.dto.UserDto(CONCAT(u.name,'JK123'),u.email,e.idCard) from User u,UserExtend e where u.id= e.userId and u.id=:id")
    UserDto findByUserDtoId(@Param("id") Long id);

    //利用接口DTO获得返回结果，需要注意的是每个字段需要as和接口里面的get方法名字保持一样
    @Query("select CONCAT(u.name,'JK123') as name,UPPER(u.email) as email ,e.idCard as idCard from User u,UserExtend e where u.id= e.userId and u.id=:id")
    UserSimpleDto findByUserSimpleDtoId(@Param("id") Long id);

    /**
     * 利用JQPl动态查询用户信息
     * @param name
     * @param email
     * @return UserSimpleDto接口
     */
    @Query("select u.name as name,u.email as email from User u where (:name is null or u.name =:name) and (:email is null or u.email =:email)")
    UserOnlyName findByUser(@Param("name") String name, @Param("email") String email);
    /**
     * 利用原始sql动态查询用户信息
     * @param user
     * @return
     */
    @Query(value = "select u.name as name,u.email as email from user u where (:#{#user.name} is null or u.name =:#{#user.name}) and (:#{#user.email} is null or u.email =:#{#user.email})",nativeQuery = true)
    UserOnlyName findByUser(@Param("user") User user);
}
