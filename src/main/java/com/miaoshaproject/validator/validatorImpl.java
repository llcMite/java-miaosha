package com.miaoshaproject.validator;

import org.springframework.beans.factory.InitializingBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class validatorImpl implements InitializingBean {
    //javax.validator
    private Validator validator;

    @Override
    public void afterPropertiesSet() throws Exception {
     //将hibernate validator通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }

    //事项校验方法返回校验结果
    public ValidationResult validate(Object bean){
        ValidationResult result=new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolationsSet=validator.validate(bean);
        if(constraintViolationsSet.size()>0){
            //有错误
            result.setHasError(true);
            /*这里有报错说不支持7及以下的语言等级，
            * file->project strcture->module->选择resource上面的language level给为7以上的
            * file->setting->compiler->java Compiler->将version改为7以上
            * */
            constraintViolationsSet.forEach(constraintViolation->{
                String errMsg=constraintViolation.getMessage();
                String propertyName=constraintViolation.getPropertyPath().toString();
                result.getErrMsgMap().put(propertyName,errMsg);
            });
        }
        return result;
    }
}
