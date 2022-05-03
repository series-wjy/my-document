package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@ToString(exclude = "userJoinColumn")
public class UserInfoJoinColumn {
   @Id
   @GeneratedValue(strategy= GenerationType.AUTO)
   private Long id;
   private Integer ages;
   private String address;
   private String telephone;
   private String idCard;

   /**
    * 指定关联字段名称
    */
   @OneToOne(cascade = {CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
   @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), name = "my_user_id")
   private UserJoinColumn userJoinColumn;
}
