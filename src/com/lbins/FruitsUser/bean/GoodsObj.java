package com.lbins.FruitsUser.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/7.
 *    "": "38",
 "": "10.00",
 "": "1",
 "": "0",
 "": "0",
 "": null,
 "": null,
 "": "10.00",
 "": ""
 */
public class GoodsObj implements Serializable{
    private String goods_id;
    private String goods_nums;
    private String goods_price;
    private String is_send;
    private String is_checkout;
    private String goods_name;
    private String product_pic;
    private String price;
    private String unit;

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_nums() {
        return goods_nums;
    }

    public void setGoods_nums(String goods_nums) {
        this.goods_nums = goods_nums;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public String getIs_send() {
        return is_send;
    }

    public void setIs_send(String is_send) {
        this.is_send = is_send;
    }

    public String getIs_checkout() {
        return is_checkout;
    }

    public void setIs_checkout(String is_checkout) {
        this.is_checkout = is_checkout;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getProduct_pic() {
        return product_pic;
    }

    public void setProduct_pic(String product_pic) {
        this.product_pic = product_pic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
