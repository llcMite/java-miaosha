package com.miaoshaproject.error;



public enum EmBusinessError implements CommonError {
    USER_NOT_EXIST(10001,"用户不存在"),
    UNKOWN_ERROR(10002,"未知错误"),
    PARAMTER_VALIDATION_ERROR(20001,"参数校验不通过")
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
