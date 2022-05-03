package com.wjy.jpa.repository;

import com.wjy.jpa.model.hierarchy.InternalEmployee;
import com.wjy.jpa.model.hierarchy.RedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalEmployeeRepository extends JpaRepository<InternalEmployee,Long>{

}
