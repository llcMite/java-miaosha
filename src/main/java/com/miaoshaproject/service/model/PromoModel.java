package com.miaoshaproject.service.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class PromoModel {
    private Integer id;
    private String promoName;
    private DateTime startDate;
    private DateTime endDate;
    private Integer itemId;
    private BigDecimal promo_item_price;
    //秒杀活动状态，1、表示还未开始  2、表示进行中 3、表示已结束
    private Integer status;

    public void setPromo_item_price(BigDecimal promo_item_price) {
        this.promo_item_price = promo_item_price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getPromo_item_price() {
        return promo_item_price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }



    public Integer getItemId() {
        return itemId;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }


}
