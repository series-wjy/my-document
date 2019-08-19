/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author wangjiayou 2019/8/19
 * @version ORAS v1.0
 */
@Data
@Entity
public class Visitor implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column
    private String ip;
    @Column
    private Integer times;
}
