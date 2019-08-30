package com.enjoy.controller;

import com.enjoy.session.MyRequestWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping(value="/login")
public class LoginController{

    @RequestMapping(value = "/toLogin", method = RequestMethod.POST)
    public String toLogin(MyRequestWrapper request, HttpServletResponse response,
                             @RequestParam("account") String account,
                          @RequestParam("passwd") String passwd){
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("account",account);

//        CookieBasedSession.onNewSession(request,response);
        return  "success";
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String getSession(MyRequestWrapper request, HttpServletResponse response){
        HttpSession httpSession = request.getSession();
//        CookieBasedSession.onNewSession(request,response);
        return  (String) httpSession.getAttribute("account");
    }

}
