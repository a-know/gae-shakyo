package com.a_know.shakyo.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import dto.AuthDTO;
import dto.AuthDTOMeta;

public class AuthController extends Controller {

    @Override
    public Navigation run() throws Exception {
        UserService userService = UserServiceFactory.getUserService();

        AuthDTO dto = new AuthDTO();
        if(userService.isUserLoggedIn()){
            dto.setLoggedIn(true);
            dto.setLogoutURL(userService.createLogoutURL("/"));
        }else{
            dto.setLoggedIn(false);
            dto.setLoginURL(userService.createLoginURL("/"));
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().println(AuthDTOMeta.get().modelToJson(dto));
        response.flushBuffer();
        return null;
    }
}
