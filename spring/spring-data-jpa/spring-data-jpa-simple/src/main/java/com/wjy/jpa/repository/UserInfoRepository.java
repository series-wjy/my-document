package com.wjy.jpa.repository;

import com.wjy.jpa.model.UserInfo;
import com.wjy.jpa.model.relation.UserInfoOneToOne;
import com.wjy.jpa.model.relation.UserInfoID;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.IdClass;

/**
 * {@link IdClass}联合主键示例
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, UserInfoID> {

}
