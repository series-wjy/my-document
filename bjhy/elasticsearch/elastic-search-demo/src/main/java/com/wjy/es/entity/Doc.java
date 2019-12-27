package com.wjy.es.entity;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.alibaba.fastjson.JSONObject;

public class Doc {
	private String index;
	private String type;
	private String id;
	//es自动带的builder
	private XContentBuilder builder;
	public XContentBuilder getBuilder() {
		return builder;
	}
	public void setBuilder(XContentBuilder builder) {
		this.builder = builder;
	}
	public JSONObject getJo() {
		return jo;
	}
	public void setJo(JSONObject jo) {
		this.jo = jo;
	}
	//json
	private JSONObject jo;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
