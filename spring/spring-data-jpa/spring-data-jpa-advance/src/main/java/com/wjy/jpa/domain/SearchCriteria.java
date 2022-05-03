package com.wjy.jpa.domain;

import lombok.*;

/**
 * @author jack，实现不同的查询条件，不同的操作，针对Value;
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
   private String key;
   private Operator operation;
   private Object value;
}
