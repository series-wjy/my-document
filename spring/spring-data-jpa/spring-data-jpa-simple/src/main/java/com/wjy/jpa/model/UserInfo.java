package com.wjy.jpa.model;

import com.wjy.jpa.model.relation.UserInfoID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Data
@Builder
@IdClass(UserInfoID.class)
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

   private Integer ages;
   @Id
   private String name;
   @Id
   private String telephone;

}
