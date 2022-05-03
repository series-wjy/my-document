package com.wjy.jpa.repository;

import com.wjy.jpa.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月06日 23:02:00
 */
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
}
