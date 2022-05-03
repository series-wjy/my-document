package com.wjy.jpa.model.hierarchy;//红皮书

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("red")
@Data
@EqualsAndHashCode(callSuper=false)
public class RedBook extends Book {

   private String redMark;

}
