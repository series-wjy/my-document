/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.dao;

import com.wjy.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wangjiayou 2019/8/19
 * @version ORAS v1.0
 */
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Visitor findByIp(String ip);
}
