package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miaoshaproject.controller.viewobject.UserVO;


import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.impl.UserServiceImpl;
import com.miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController{
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/getuser",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getUserById(@RequestParam(name="id")Integer id)throws BusinessException{
        UserVO userVO=new UserVO();
        UserModel userModel=userService.getUserById(id);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVO obj=converUserVOFromUserModel(userModel);
        return CommonReturnType.create(obj);
    }

    public UserVO converUserVOFromUserModel(UserModel userModel){
        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

    //用户获取opt短信验证码
    @RequestMapping(value="/getotp",method={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
        //需要按照一定规则生成otp验证码
        Random random=new Random();
        int randomInt=random.nextInt(99999);
        randomInt += 10000;
        String otpCode=String.valueOf(randomInt);
        //将opt验证码同对应手机号关联，redis可用于分布式，
        // 这里使用httpsession的方式绑定手机号和code
        this.httpServletRequest.getSession().setAttribute(telphone,otpCode);

        //将opt验证码通过短信通道发送给用户,
        // 这里省略使用控制台的方式打印code码,在企业里开发这种做法是不允许的，
        // 这些是敏感信息
        System.out.println("telphone = " + telphone + "& otpCode = " + otpCode);
        return CommonReturnType.create(null);
    }

    //用户登入功能
    @RequestMapping(value="/login",method={RequestMethod.POST},consumes = {CONTENT_TYPE_JSON})
    @ResponseBody
    public CommonReturnType login(@RequestBody String jsonStr) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        JSONObject requestJson = JSON.parseObject(jsonStr);
        String telphone=(String)requestJson.get("telphone");
        String password=(String)requestJson.get("password");
        //入参校验
        if(StringUtils.isEmpty(telphone)||
        StringUtils.isEmpty(password) ){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR);
        }
        //验证登入是否合法
         UserModel userModel = userService.validateLogin(telphone,
                 this.EncodeByMd5(password));
        //添加登入成功的session
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);

    }
    //用户登入功能
    @RequestMapping(value="/loginOut",method={RequestMethod.GET})
    @ResponseBody
    public CommonReturnType loginOut(@RequestParam(name="telphone")String telphone) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //添加登入成功的session
        this.httpServletRequest.getSession().removeAttribute("IS_LOGIN");
        this.httpServletRequest.getSession().removeAttribute("LOGIN_USER");
        return CommonReturnType.create(null);

    }
    //用户注册功能
    @RequestMapping(value="/register",method={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone")String telphone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="name")String name,
                                     @RequestParam(name="gender")String gender,//性别
                                     @RequestParam(name="age")Integer age,
                                     @RequestParam(name="password")String password
                                     ) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpcode相符合
        String inSesstionOtpCode=(String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSesstionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"短信验证码错误");
        }

        //用户的注册流程
        UserModel userModel=new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEnCrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);

        return CommonReturnType.create(null);
    }
    //用户列表
    @RequestMapping(value = "/getUserList",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getUserList()throws BusinessException{
        List<UserModel> userModelList=userService.getUserList();
        ListIterator userListIt=userModelList.listIterator();
        List<UserVO> userVOList=new ArrayList<>();

        while(userListIt.hasNext()){
            UserModel userModel = (UserModel) userListIt.next();
            UserVO userVO=converUserVOFromUserModel(userModel);
            userVOList.add(userVO);
        }
        return CommonReturnType.create(userVOList);
    }

    //md5的加密计算
    //java自带的md5只支持16位
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en= new BASE64Encoder();
        String newStr =base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }
}
