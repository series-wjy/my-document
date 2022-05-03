package com.wjy.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月04日 15:34:00
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    private String id;
    private String name;
    private String firstname;
    private String lastname;
    @OneToOne
    private EmailAddress emailAddress;
}
