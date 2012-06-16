package com.a_know.shakyo.controller;

import org.slim3.controller.router.RouterImpl;


public class AppRouter extends RouterImpl {
    public AppRouter(){
        addRouting("/_ah/prospective_search?{params}", "/prospectiveSearch?{params}");
    }
}
