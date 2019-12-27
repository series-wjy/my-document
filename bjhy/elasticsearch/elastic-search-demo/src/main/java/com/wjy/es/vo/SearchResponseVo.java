package com.wjy.es.vo;

/**
 * 查询使用的vo
 */
public class SearchResponseVo implements java.io.Serializable{
	 
	private static final long serialVersionUID = 1682986896598864270L;
	private String value;
	private String order;
	private int sizÍe;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public int getSizÍe() {
		return sizÍe;
	}
	public void setSizÍe(int sizÍe) {
		this.sizÍe = sizÍe;
	}
}
