package com.wjy.jpa.repository;

import com.wjy.jpa.model.EmailAddress;
import com.wjy.jpa.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PersonRepository extends Repository<Person, String> {

   // and 的查询关系
   List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

   // 包含 distinct 去重，or 的 sql 语法
   List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);

   // 根据 lastname 字段查询忽略大小写
   List<Person> findByLastnameIgnoreCase(String lastname);

   // 根据 lastname 和 firstname 查询 equal 并且忽略大小写
   List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

   // 对查询结果根据 lastname 排序，正序
   List<Person> findByLastnameOrderByFirstnameAsc(String lastname);

   // 对查询结果根据 lastname 排序，倒序
   List<Person> findByLastnameOrderByFirstnameDesc(String lastname);


   Page<Person> findByLastname(String lastname, Pageable pageable);//根据分页参数查询User，返回一个带分页结果的Page(下一课时详解)对象（方法一）

//   Slice<User> findByLastname(String lastname, Pageable pageable);//我们根据分页参数返回一个Slice的user结果（方法二）
//
//   List<User> findByLastname(String lastname, Sort sort);//根据排序结果返回一个List（方法三）
//
//   List<User> findByLastname(String lastname, Pageable pageable);//根据分页参数返回一个List对象（方法四）
}
