package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * {@link OneToOne} 使用示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月04日 15:34:00
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String email;
    private String sex;

}
