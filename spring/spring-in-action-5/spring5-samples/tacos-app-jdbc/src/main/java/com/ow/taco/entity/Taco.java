package com.ow.taco.entity;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

// tag::newFields[]
@Data
public class Taco {

  private Long id;
  
  private Date createdAt;

//end::newFields[]

  @Size(min=5, message="Name must be at least 5 characters long")
  private String name;

  @Size(min=1, message="You must choose at least 1 ingredient")
  private List<Ingredient> ingredients;

  /*
//tag::newFields[]
   ...
   
//end::newFields[]
   */
//tag::newFields[]
}
//end::newFields[]
