package com.wjy.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserExtend { //用户扩展信息表
   @Id
   @GeneratedValue(strategy= GenerationType.AUTO)
   private Long id;
   private Long userId;
   private String idCard;
   private Integer ages;
   private String studentNumber;
}