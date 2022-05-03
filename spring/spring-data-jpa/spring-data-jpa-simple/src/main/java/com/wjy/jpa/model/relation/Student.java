package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年12月08日 22:21:00
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
//    @ManyToMany
//    @JoinTable(name = "student_teacher_ref",
//            joinColumns = @JoinColumn(name = "stu_id"),
//            inverseJoinColumns = @JoinColumn(name="tea_id"))
//    private List<Teacher> teachers;
    @OneToMany(mappedBy = "student")
    private List<TeacherStudentRelation> teacherStudentRelations;
}
