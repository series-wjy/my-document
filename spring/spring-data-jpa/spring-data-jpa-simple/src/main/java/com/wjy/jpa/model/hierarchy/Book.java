package com.wjy.jpa.model.hierarchy;

import lombok.Data;

import javax.persistence.*;

/**
 * 单表多态问题，同一张 Table，表示了不同的对象，通过一个字段来进行区分。
 * 利用@Inheritance(strategy = InheritanceType.SINGLE_TABLE)注解完成，只有父类有 @Table。
 */
@Entity(name="book")
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="color", discriminatorType = DiscriminatorType.STRING)
public class Book {

   @Id
//   @GeneratedValue(strategy= GenerationType.AUTO)
   private Long id;
   private String title;
}
