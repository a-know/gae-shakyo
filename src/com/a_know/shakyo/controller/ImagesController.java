package com.a_know.shakyo.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.util.StringUtil;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

import dto.Profile;
import dto.ProfileMeta;
import dto.UploadUrlDTO;
import dto.UploadUrlDTOMeta;

public class ImagesController extends Controller {

    static final String PATH = "/images";

    @Override
    protected Navigation run() throws Exception{
        if(isGet()){
            return doGet();
        }else{
            return doPost();
        }
    }

    Navigation doPost() throws Exception {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        Entry<String, List<BlobKey>> next = blobs.entrySet().iterator().next();
        System.out.println("@@@responseUploadUrl");
        return redirect(PATH + "?key=" + next.getValue().get(0).getKeyString());
    }

    private Navigation responseForjQueryUploadPlugin() throws Exception{
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        if(currentUser == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        BlobKey blobKey = new BlobKey(asString("key"));
        String servingUrl = ImagesServiceFactory.getImagesService().getServingUrl(blobKey, 32, false);

        Profile profile = new Profile();
        profile.setKey(Datastore.createKey(Profile.class, String.valueOf(currentUser.getUserId())));
        profile.setProfileUrl(servingUrl);
        Datastore.put(profile);

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
        response.getWriter().println(ProfileMeta.get().modelToJson(profile));
        response.flushBuffer();
        System.out.println("@@@responseForjQueryUploadPlugin");
        return null;
    }

    private Navigation responseUploadUrl() throws Exception{
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        if(currentUser == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        String uploadUrl = BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(ImagesController.PATH);

        UploadUrlDTO dto = new UploadUrlDTO();
        dto.setUploadUrl(uploadUrl);

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().println(UploadUrlDTOMeta.get().modelToJson(dto));
        response.flushBuffer();
        return null;
    }

    Navigation doGet() throws Exception {
        if(StringUtil.isEmpty(asString("key")) == false){
            return responseForjQueryUploadPlugin();//jQuery.pluginへの（アップロード後の）レスポンス
        }else if(StringUtil.isEmpty(asString("user")) == false){
            return redirectToProfileImage();
        }else{
            return responseUploadUrl();
        }
    }
    private Navigation redirectToProfileImage(){
        Key profileKey = Datastore.createKey(Profile.class, asString("user"));
        Profile p = Datastore.getOrNull(Profile.class, profileKey);
        if(p == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return redirect(p.getProfileUrl());
    }
}
