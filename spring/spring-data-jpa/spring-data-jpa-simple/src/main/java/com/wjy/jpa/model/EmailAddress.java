package com.wjy.jpa.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Entity;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月05日 14:24:00
 */
@Entity
public class EmailAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long Id;
    private String emailAddress;
}
