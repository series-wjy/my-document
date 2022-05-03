package com.wjy.jpa.domain;

import org.springframework.util.StringUtils;

public enum Operator {

   /**
    * 等于
    */
   EQ("="),
   /**
    * 等于
    */
   LK(":"),
   /**
    * 不等于
    */
   NE("!="),
   /**
    * 大于
    */
   GT(">"),
   /**
    * 小于
    */
   LT("<"),

   /**
    * 大于等于
    */
   GE(">=");

   Operator(String operator) {
      this.operator = operator;
   }
   private String operator;

   public static Operator fromOperator(String group) {
      for(Operator opt : Operator.values()){
         if(opt.operator.equals(group)){
            return opt;
         }
      }
      return null;
   }
}
