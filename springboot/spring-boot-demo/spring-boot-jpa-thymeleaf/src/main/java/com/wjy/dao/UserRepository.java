package com.wjy.dao;

import com.wjy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);
    void deleteById(Long id);
}