/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.jpa.dao;

import com.wjy.jpa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wangjiayou 2019/8/1
 * @version ORAS v1.0
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Page<User> findByUserName(String userName, Pageable pageable);

    @Modifying
    @Query("update User u set u.userName = ?1 where u.id = ?2")
    int modifyByIdAndUserId(String  userName, Long id);

    @Transactional
    @Modifying
    @Query("delete from User where id = ?1")
    void deleteByUserId(Long id);

    @Transactional(timeout = 10)
    @Query("select u from User u where u.email = ?1")
    User findByEmail(String email);
}
