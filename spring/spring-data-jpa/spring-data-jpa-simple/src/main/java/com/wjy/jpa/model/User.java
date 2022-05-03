package com.wjy.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.List;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String email;
    private String sex;
    private String address;
    private String idCard;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserAddress> userAddresses;

    @Version
    private Long version;
}
