package com.wjy.jpa.repository;

import com.wjy.jpa.domain.UserQbe;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserQbeRepository extends JpaRepository<UserQbe,Long>, JpaSpecificationExecutor<UserQbe> {

}
