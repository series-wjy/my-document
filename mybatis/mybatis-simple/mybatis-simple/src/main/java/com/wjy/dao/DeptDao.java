package com.wjy.dao;

import com.wjy.beans.Dept;

public interface DeptDao {

	public Dept findByDname(String Dname);
	public Dept findByDeptNo(Integer deptno);
	
}
