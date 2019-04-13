package com.miaoshaproject.response;

public class CommonReturnType {
    private String status;
    private Object data;

    public static CommonReturnType create(Object data){
        return CommonReturnType.create(data,"success");
    }

    public static CommonReturnType create(Object data,String status){
        CommonReturnType type=new CommonReturnType();
        type.setData(data);
        type.setStatus(status);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
