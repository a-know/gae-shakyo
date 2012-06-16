package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.TestEnvironment;

import com.google.apphosting.api.ApiProxy;

import dto.AuthDTO;
import dto.AuthDTOMeta;

public class AuthControllerTest extends ControllerTestCase {

    static final String PATH = "/auth";

    @Test
    public void ログインしていない() throws Exception {

        tester.start(PATH);

        assertThat("AuthControllerのインスタンスが使用される", tester.getController(), instanceOf(AuthController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));

        String json = tester.response.getOutputAsString();
        AuthDTO dto = AuthDTOMeta.get().jsonToModel(json);

        assertThat("ログイン中でない", dto.isLoggedIn(), is(false));
        assertThat("ログイン用URLが返される", dto.getLoginURL(), is(notNullValue()));
        assertThat("ログアウト用URLは返されない", dto.getLogoutURL(), is(nullValue()));
    }

    @Test
    public void ログインしている() throws Exception {
        //ログイン状態の作成
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        tester.start(PATH);

        assertThat("AuthControllerのインスタンスが使用される", tester.getController(), instanceOf(AuthController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));

        String json = tester.response.getOutputAsString();
        AuthDTO dto = AuthDTOMeta.get().jsonToModel(json);

        assertThat("ログイン中", dto.isLoggedIn(), is(true));
        assertThat("ログインURLは返されない", dto.getLoginURL(), is(nullValue()));
        assertThat("ログアウトURLが返される", dto.getLogoutURL(), is(notNullValue()));
    }
}
