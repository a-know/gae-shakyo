package com.a_know.shakyo.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.util.StringUtil;

import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.model.Memo;
import com.a_know.shakyo.service.MemoService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.UserServiceFactory;

public class MemoController extends Controller {

    @Override
    public Navigation run() throws Exception {
        if(isPost()){
            return doPost();
        }else{
            return doGet();
        }
    }

    private Navigation doGet() throws Exception{
        Key minutesKey;
        try{
            minutesKey = asKey("minutes");
            if(minutesKey == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
        }catch(IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        List<Memo> list = MemoService.list(minutesKey);

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(MemoMeta.get().modelsToJson(list));
        response.flushBuffer();

        //アクセスカウント用に、access-logキューにpullタスクを追加
        QueueFactory.getQueue("access-log").add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).param("minutesKey", Datastore.keyToString(minutesKey)));

        return null;
    }
    private Navigation doPost() throws Exception{
        if(UserServiceFactory.getUserService().getCurrentUser() == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        String memo = asString("memo");
        String minutesString = asString("minutes");

        if(StringUtil.isEmpty(memo)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if(StringUtil.isEmpty(minutesString)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Key minutesKey;
        try{
            minutesKey = Datastore.stringToKey(minutesString);
        }catch(IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Key memoKey = MemoService.put(minutesKey, memo);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        ChannelController.pushMemo(memoKey);
        return null;
    }
}
