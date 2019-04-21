package com.miaoshaproject.service.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ItemModel {
    private int id;
    @NotBlank(message = "商品名称不能为空")
    private String title;
    @NotNull
    @Min(value=0,message="商品价格必须大于0")
    private BigDecimal price;
    @NotNull(message="库存不能为空")
    private Integer stock;

    private String description;
    @NotNull(message="销售额不能为空")
    private int sales;
    @NotBlank(message="url不能为空")
    private String imgUrl;
    //使用聚合模型,如果promoModel不为空，则表示其拥有还未结束的秒杀活动
    private PromoModel promoModel;

    public PromoModel getPromoModel() {
        return promoModel;
    }

    public void setPromoModel(PromoModel promoModel) {
        this.promoModel = promoModel;
    }



    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescript() {
        return description;
    }

    public void setDescript(String descript) {
        this.description = descript;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
