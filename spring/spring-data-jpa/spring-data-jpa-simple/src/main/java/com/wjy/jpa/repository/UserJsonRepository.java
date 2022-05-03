package com.wjy.jpa.repository;

import com.wjy.jpa.model.UserJson;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Jackson示例
 */
public interface UserJsonRepository extends JpaRepository<UserJson, Long> {

}
