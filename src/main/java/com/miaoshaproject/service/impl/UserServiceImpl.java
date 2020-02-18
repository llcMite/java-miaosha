package com.miaoshaproject.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;

import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;
    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(int id) {
        UserDO userDO=userDOMapper.selectByPrimaryKey(id);
        UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(id);
        return converFromUserDO(userDO,userPasswordDO);
    }

    public UserModel converFromUserDO (UserDO userDO,UserPasswordDO userPasswordDO){
        UserModel userModel=new UserModel();
        if(userDO==null){
            return null;
        }
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO != null){
            userModel.setEnCrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        //这里需要严谨一些，对所有字段判断不为空
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR);
        }
        ValidationResult result=validator.validate(userModel);
        if(result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,result.getErrMsg());
        }

        //这里需要注意的点是在数据库里设置索引手机号是唯一的
        try{
            UserDO userDO=convertFromModel(userModel);
            userDOMapper.insertSelective(userDO);
        }catch(DuplicateKeyException ex){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"手机号已重复注册");
        }
        UserDO newUserDO=userDOMapper.selectByTelphone(userModel.getTelphone());
        userModel.setId(newUserDO.getId());
        UserPasswordDO userPasswordDO=convertPasswordFormUserModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
        return;
    }
    private UserPasswordDO convertPasswordFormUserModel(UserModel userModel){
       if(userModel==null){
           return null;
       }
       UserPasswordDO userPasswordDO=new UserPasswordDO();
       userPasswordDO.setEncrptPassword(userModel.getEnCrptPassword());

       userPasswordDO.setUserId(userModel.getId());
       return userPasswordDO;
    }
    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    //登入是否合法

    @Override
    public UserModel validateLogin(String telphone, String password) throws BusinessException {
        //通过手机获取用户信息，新增查询方式通过手机来获取用户信息
         UserDO userDO=userDOMapper.selectByTelphone(telphone);
         if(userDO==null){
             throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"用户不存在");
         }
         UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId( userDO.getId());

        //比对用户信息内加密的密码是否和传输进来的一致
        if(!StringUtils.equals(userPasswordDO.getEncrptPassword(),password)){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"账号或密码不存在");
        }
        UserModel userModel=converFromUserDO(userDO,userPasswordDO);
        return userModel;
    }

    @Override
    public List<UserModel> getUserList() throws BusinessException {
        List<UserDO> userList= userDOMapper.getUserList();
        List<UserModel> userModelList=new ArrayList<>();
        ListIterator userIt1 = userList.listIterator();
        while(userIt1.hasNext()){
            UserDO userDO = (UserDO) userIt1.next();
            UserModel userModel=converFromUserDO(userDO,null);
            userModelList.add(userModel);
        }
        return userModelList;
    }
}
