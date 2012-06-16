package com.a_know.shakyo.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.util.StringUtil;

import com.a_know.shakyo.meta.MinutesMeta;
import com.a_know.shakyo.model.Minutes;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class MinutesController extends Controller {

    @Override
    public Navigation run() throws Exception {

        if(isPost()){
            if(UserServiceFactory.getUserService().isUserLoggedIn() == false){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }
            return doPost();
        }else if(StringUtil.isEmpty(asString("download")) == false){
            return doDownload();
        }else if(StringUtil.isEmpty(asString("delete")) == false){
            return doDelete();
        }else{
            return doGet();
        }
    }

    private Navigation doGet() throws Exception{
        List<Minutes> minutes = MinutesService.list();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(MinutesMeta.get().modelsToJson(minutes));
        response.flushBuffer();
        return null;
    }
    private Navigation doPost() throws Exception {
        if(UserServiceFactory.getUserService().getCurrentUser() == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        String title = asString("title");
        if(StringUtil.isEmpty(title)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Key key = MinutesService.put(title);
        sendMail(KeyFactory.keyToString(key));
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return null;
    }

    private Navigation doDownload() throws IOException{
        String blobKeyString = asString("download");
        if(StringUtil.isEmpty(blobKeyString)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        BlobKey blobKey = new BlobKey(blobKeyString);
        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

        response.setContentType(blobInfo.getContentType());
        response.setContentLength((int) blobInfo.getSize());
        response.setHeader("Content-disposition", "attachment; " + blobInfo.getFilename());

        byte[] data = blobstoreService.fetchData(blobKey, 0, blobInfo.getSize());
        response.getOutputStream().write(data);
        response.flushBuffer();
        return null;
    }

    private Navigation doDelete() throws IOException{
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        if(currentUser == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        Key minutesKey;
        try{
            minutesKey = asKey("delete");
            if(minutesKey == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
        }catch(IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Minutes minutes = Datastore.getOrNull(Minutes.class, minutesKey);

        if(minutes == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if(minutes.getAuthor().equals(currentUser) == false){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        //TSV作成と削除する
        BlobKey blobKey = MinutesService.exportAsTsv(minutes);
        MinutesService.deleteMinutes(minutes);

        //ダウンロードURLをメールで送信する
        Message m = new Message();
        m.setSender("minutes@gae-shakyo.appspotmail.com");
        m.setSubject("議事録[" + minutes.getTitle() + "]がTSVに変換されました");
        m.setTo(currentUser.getEmail());
        StringBuilder b = new StringBuilder();
        b.append(request.getScheme()).append("://").append(request.getServerName());
        if(request.getServerPort() != 80){
            b.append(":").append(request.getServerPort());
        }
        b.append("/minutes?download=").append(blobKey.getKeyString());
        m.setTextBody(b.toString());

        MailService mailService = MailServiceFactory.getMailService();
        mailService.send(m);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return null;
    }

    private void sendMail(String keyString) throws IOException{
        Message message = new Message();
        message.setSender("minutes@gae-shakyo.appspotmail.com");
        message.setSubject("新しい議事録が追加されました。");
        StringBuilder b = new StringBuilder();
        b.append(request.getScheme()).append("://").append(request.getServerName());
        if(request.getServerPort() != 80){
            b.append(":").append(request.getServerPort());
        }
        b.append("/minutes.html?minutes=").append(keyString);
        message.setTextBody(b.toString());

        MailService mailService = MailServiceFactory.getMailService();
        mailService.sendToAdmins(message);
    }
}
