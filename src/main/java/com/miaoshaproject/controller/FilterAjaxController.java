package com.miaoshaproject.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Component
public class FilterAjaxController implements Filter{
    @Autowired
    private HttpServletRequest httpServletRequest;
    public FilterAjaxController() {
        super();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("time filter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("time filter start");
        long start = new Date().getTime();
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;
        Boolean isLogin = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");


        String url=httpServletRequest.getRequestURI();
        ArrayList alowUrlArr=getAlowAjaxUrl(url);
        if( (isLogin == null||!isLogin.booleanValue())&& !alowUrlArr.contains(url)){
            ((HttpServletResponse) servletResponse).setHeader("sessionstatus","timeout");
        }else{
            filterChain.doFilter(servletRequest, servletResponse);
            System.out.println("time filter 耗时："+(new Date().getTime()-start));
            System.out.println("time filter finish");
        }

    }

    @Override
    public void destroy() {
        System.out.println("time filter destroy");
    }

    public ArrayList getAlowAjaxUrl(String url){
        ArrayList alowUrlArr=new ArrayList<>();
        alowUrlArr.add("/user/login");
        alowUrlArr.add("/user/loginOut");
        return alowUrlArr;
    }
}
