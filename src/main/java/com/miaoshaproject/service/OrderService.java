package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

public interface OrderService {
    //使用1.通过前端url上传过来的秒杀id，然后下单接口校验对应商品对应活动一开始
    //2.直接在下单接口判断对应的商品是否存在秒杀活动，若存在则以秒杀价格下单
    OrderModel createModel(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException;

}
