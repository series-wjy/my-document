package com.wjy.jpa.repository;

import com.wjy.jpa.model.hierarchy.InternalEmployee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.wjy.jpa.model.hierarchy.Employee.Level.SENIOR;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InternalEmployeeRepositoryTest {

    @Autowired
    private InternalEmployeeRepository internalEmployeeRepository;

    @Test
    public void testRedBook() {
        InternalEmployee employee = new InternalEmployee();
        employee.setId(1L);
        employee.setLevel(SENIOR);
        employee.setNational("中国");
        internalEmployeeRepository.saveAndFlush(employee);
        InternalEmployee r = internalEmployeeRepository.findById(1L).get();
        System.out.println(r.getId() + ":" + r.getNational() + ":" + r.getLevel());

    }

}
