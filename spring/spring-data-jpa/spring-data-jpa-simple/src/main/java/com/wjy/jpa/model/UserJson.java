package com.wjy.jpa.model;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

import javax.persistence.*;

import java.time.Instant;

import java.util.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"createDate","email"})
public class UserJson {

   @Id
   @GeneratedValue(strategy= GenerationType.AUTO)
   private Long id;

   @JsonProperty("my_name")
   private String name;
   private Instant createDate;
   @JsonFormat(timezone ="GMT+8", pattern = "yyyy-MM-dd HH:mm")
   private Date updateDate;
   private String email;
   @JsonIgnore
   private String sex;
   @JsonCreator
   public UserJson(@JsonProperty("email") String email) {
      System.out.println("其他业务逻辑");
      this.email = email;
   }

   @Transient
   @JsonAnySetter
   private Map<String,Object> other = new HashMap<>();

   @JsonAnyGetter
   public Map<String, Object> getOther() {
      return other;
   }
}
