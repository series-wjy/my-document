package com.wjy.jpa.model.hierarchy;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月12日 11:46:00
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@PrimaryKeyJoinColumn(name = "employee_id", referencedColumnName = "id")
@Data
public class InternalEmployee extends Employee {
    private String national;
}
