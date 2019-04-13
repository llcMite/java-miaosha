package com.miaoshaproject.validator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    private boolean hasError = false;

    //存放错误信息
    private Map<String,String> errMsgMap = new HashMap<>();

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public Map<String, String> getErrMsgMap() {
        return errMsgMap;
    }

    public void setErrMsgMap(Map<String, String> errMsgMap) {
        this.errMsgMap = errMsgMap;
    }
    //实现通用的格式化字符串信息获取错误结果的msg方法
    public String getErrMsg(){
        return StringUtils.join(errMsgMap.values().toArray(),",");
    }
}
