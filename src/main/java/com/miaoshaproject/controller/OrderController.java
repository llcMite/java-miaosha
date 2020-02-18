package com.miaoshaproject.controller;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.impl.OrderServiceImpl;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OrderServiceImpl orderService;

    @RequestMapping(value="create",method = RequestMethod.POST,consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType createOrder (@RequestParam(name="itemId")Integer itemId,
                                         @RequestParam(name="amount")Integer amount,
                                         @RequestParam(name="promoId",required = false)Integer promoId
    ) throws BusinessException {
        Boolean isLogin = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null||!isLogin.booleanValue()){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登入不能下单");
        }
        UserModel userModel=(UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        //进行下单操作
        OrderModel orderModel=orderService.createModel(userModel.getId(),itemId,promoId,amount);

        return CommonReturnType.create(null);
    }
}
