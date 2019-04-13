package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {
    UserModel getUserById(int id);
    public void register(UserModel userModel) throws BusinessException;
    public UserModel validateLogin(String telphone,String password) throws BusinessException;
}
