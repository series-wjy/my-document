package com.wjy.jpa.model.hierarchy;

import lombok.Data;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月12日 11:33:00
 */
@Entity
@Table(name = "employee")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Employee {
    @Id
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    private Level level;

    public enum Level {
        SENIOR("高级"), MEDIUM("中级"), JUNIOR("初级");
        private String value;
        Level(String value) {
            this.value = value;
        }
    }
}

