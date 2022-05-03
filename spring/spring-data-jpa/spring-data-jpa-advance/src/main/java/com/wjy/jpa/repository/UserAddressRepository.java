package com.wjy.jpa.repository;

import com.wjy.jpa.domain.UserAddressQbe;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddressQbe,Long> {

}