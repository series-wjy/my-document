package com.wjy.vo;

import java.io.File;
import java.io.FileInputStream;

public class FileInfoVo implements java.io.Serializable{
	private static final long serialVersionUID = -7782665556358408062L;
	private int id;
	private String name;
	private FileInputStream in;

	public FileInputStream getIn() {
		return in;
	}

	public void setIn(FileInputStream in) {
		this.in = in;
	}

	public FileInfoVo() {
	}

	public FileInfoVo(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "BookVo [id=" + id + ", name=" + name + "]";
	}
	
	
}
