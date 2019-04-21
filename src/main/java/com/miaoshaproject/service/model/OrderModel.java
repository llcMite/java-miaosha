package com.miaoshaproject.service.model;

import java.math.BigDecimal;

public class OrderModel {
    private String id;
    private Integer userId;
    private Integer itemId;
    private Integer amount;
    //如果秒杀id存在，则使用的是秒杀价格下单
    private BigDecimal orderAmount;
    //如果秒杀id存在，则使用的是秒杀价格下单
    private BigDecimal itemPrice;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    //如果秒杀id非空，则表示已秒杀的方式下单
    private Integer promoId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
}
