package com.wjy.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import java.math.BigDecimal;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月24日 09:39:00
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptimisticLock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "myId")
    @GenericGenerator(name = "myId", strategy = "com.wjy.jpa.model.generator.ManualInsertGenerator")
    private int id;
    private int money;
    @Version
    private int version;
}
