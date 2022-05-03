package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月08日 22:22:00
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
//    @ManyToMany(mappedBy = "teachers")
//    private List<Student> students;

    @OneToMany(mappedBy = "teacher")
    private List<TeacherStudentRelation> teacherStudentRelations;
}
