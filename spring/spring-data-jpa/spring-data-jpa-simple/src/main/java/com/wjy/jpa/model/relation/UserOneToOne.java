package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;

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
public class UserOneToOne {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String email;
    private String sex;

    /**
     * 双向关联关系必须要配置这个注解
     * mappedBy 不能与 @JoinColumn 或者 @JoinTable 同时使用，因为没有意义，关联关系不在这里面维护。
     */
    @OneToOne(mappedBy = "userOneToOne")
    private UserInfoOneToOne userInfoOneToOne;
}
