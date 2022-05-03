package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;

import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * {@link IdClass}联合主键
 * @Create: 2020/11/11 10:39
 * @Author: wangjiayou
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoID implements Serializable {

   private String name,telephone;

}
