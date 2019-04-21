package com.miaoshaproject.error;



public enum EmBusinessError implements CommonError {
    USER_NOT_EXIST(10001,"用户不存在"),
    USER_lOGIN_FIAL(20002,"用户手机号或密码不正确"),
    USER_NOT_LOGIN(20003,"用户未登入"),
    UNKOWN_ERROR(10002,"未知错误"),
    PARAMTER_VALIDATION_ERROR(20001,"参数校验不通过"),
    STOCK_NOT_ENOUGH(30000,"库存不足");
    ;
    private int errCode;
    private String errMsg;

    private EmBusinessError(int errCode,String errMsg){
        this.errCode=errCode;
        this.errMsg=errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg=errMsg;
        return this;
    }
}
