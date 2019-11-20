package com.wjy.es.entity;

public class Product implements java.io.Serializable {
	 
	private static final long serialVersionUID = 9049724072645117741L;
	private Integer id;
	private String title;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
