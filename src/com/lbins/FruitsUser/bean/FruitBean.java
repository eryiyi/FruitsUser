package com.lbins.FruitsUser.bean;


import java.io.Serializable;
import java.util.List;
public class FruitBean implements Serializable{

	private String address;
	private String send_date;
	private List<GoodsObj> goods_data;


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSend_date() {
		return send_date;
	}

	public void setSend_date(String send_date) {
		this.send_date = send_date;
	}

	public List<GoodsObj> getGoods_data() {
		return goods_data;
	}

	public void setGoods_data(List<GoodsObj> goods_data) {
		this.goods_data = goods_data;
	}
}
