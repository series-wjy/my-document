package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
@Data
@Builder
//@IdClass(UserInfoID.class)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "userOneToOne")
public class UserInfoOneToOne {
   @Id
   @GeneratedValue(strategy= GenerationType.AUTO)
   private Long id;
//   @Id // 指定 id 字段
//   private long userOneToOneId;
   private Integer ages;
   private String address;
   private String telephone;
   private String idCard;

//   @MapsId
   @OneToOne(cascade = {CascadeType.PERSIST}, orphanRemoval = true) // 只配置 @OneToOne 是单向关联关系
   private UserOneToOne userOneToOne;

//   @Column(name = "user_one_to_one_id") // 用于只查询关联字段，不查询关联实体
//   private String userOneToOneId;
}
