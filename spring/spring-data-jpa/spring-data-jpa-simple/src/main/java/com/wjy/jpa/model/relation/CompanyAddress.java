package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月08日 21:53:00
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String zip;
}
