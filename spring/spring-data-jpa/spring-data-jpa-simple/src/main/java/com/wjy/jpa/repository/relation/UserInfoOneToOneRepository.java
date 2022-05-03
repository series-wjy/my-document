package com.wjy.jpa.repository.relation;

import com.wjy.jpa.model.relation.UserInfoID;
import com.wjy.jpa.model.relation.UserInfoOneToOne;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月01日 23:48:00
 */
public interface UserInfoOneToOneRepository extends JpaRepository<UserInfoOneToOne, Long> {
}
