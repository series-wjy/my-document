package com.wjy.jpa.model.relation;

import lombok.*;

import javax.persistence.*;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherStudentRelation {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private Date createTime,udpateTime;
   @ManyToOne
   private Teacher teacher;
   @ManyToOne
   private Student student;

}
