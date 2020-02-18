package com.miaoshaproject.controller;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//在跨域的情况下session是不支持共享的，前端也需要设置xhrFields:{widthCredentials:true}
public class BaseController {
    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON ="application/json";
    @ExceptionHandler
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public CommonReturnType handlerException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException){
           BusinessException businessException = (BusinessException)ex;
           responseData.put("errCode",businessException.getErrCode());
           responseData.put("errMsg",businessException.getErrMsg());
        }else{
           responseData.put("errCode", EmBusinessError.UNKOWN_ERROR.getErrCode());
           responseData.put("errMsg",EmBusinessError.UNKOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}
