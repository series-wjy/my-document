package com.wjy.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    public Student(String name) {
        this.name = name;
    }
}