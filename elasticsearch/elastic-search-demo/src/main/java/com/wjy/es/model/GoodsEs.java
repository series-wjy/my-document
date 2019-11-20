package com.wjy.es.model;

import java.util.Date;

public class GoodsEs implements java.io.Serializable{
	 
	private static final long serialVersionUID = -2976728810236574345L;
	private Integer goodsId;
	private String goodsName;
	private Integer productId;
	private Integer shopId;
	private Integer costPrice;
	private Integer sellPrice;
	private Integer inventory;
	private String specInfo;
	private Date createTime;
	private Integer brand;
	private Integer classify1;
	private Integer classify2;
	private Integer classify3;
	private String subtitle;
	private String productName;
	private String imgPath;
	private String template;
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public Integer getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getShopId() {
		return shopId;
	}
	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}
	public Integer getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(Integer costPrice) {
		this.costPrice = costPrice;
	}
	public Integer getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(Integer sellPrice) {
		this.sellPrice = sellPrice;
	}
	public Integer getInventory() {
		return inventory;
	}
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
	public String getSpecInfo() {
		return specInfo;
	}
	public void setSpecInfo(String specInfo) {
		this.specInfo = specInfo;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getBrand() {
		return brand;
	}
	public void setBrand(Integer brand) {
		this.brand = brand;
	}
	public Integer getClassify1() {
		return classify1;
	}
	public void setClassify1(Integer classify1) {
		this.classify1 = classify1;
	}
	public Integer getClassify2() {
		return classify2;
	}
	public void setClassify2(Integer classify2) {
		this.classify2 = classify2;
	}
	public Integer getClassify3() {
		return classify3;
	}
	public void setClassify3(Integer classify3) {
		this.classify3 = classify3;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
}
