package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.TestEnvironment;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.apphosting.api.ApiProxy;

import dto.Profile;
import dto.ProfileMeta;
import dto.UploadUrlDTO;
import dto.UploadUrlDTOMeta;

public class ImagesControllerTest extends ControllerTestCase {

    static final String PATH = "/images";

    @Test
    public void アップロード用URLを取得する_ログインしていない() throws Exception{
        tester.start(PATH);
        assertThat("レスポンスコードが401", tester.response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    public void アップロード用URLを取得する() throws Exception{
        //ログイン状態の作成
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        tester.start(PATH);

        assertThat("ImagesControllerのインスタンスが使用される", tester.getController(), instanceOf(ImagesController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat("Content-Typeのチェック", tester.response.getContentType(), is("application/json"));
        assertThat("Character-Encodingのチェック", tester.response.getCharacterEncoding(), is("utf-8"));
        UploadUrlDTO dto = UploadUrlDTOMeta.get().jsonToModel(tester.response.getOutputAsString());
        assertThat("uploadUrlが返される", dto.getUploadUrl(), is(notNullValue()));
    }

    @Test
    public void リダイレクト後のjQueryUploadPlugin向けのレスポンス() throws Exception {
        //Blobstoreにテスト用の画像を追加し、パラメータ用のBlobKeyを取得する
        File testImageFile = new File("C:\\Users\\a-know\\Dropbox\\dev\\workspace\\gae-shakyo\\war\\test\\a-know-icon.png");
        byte[] imageBytes = new byte[(int) testImageFile.length()];
        DataInputStream input = new DataInputStream(new FileInputStream(testImageFile));
        input.readFully(imageBytes);
        input.close();

        FileService fileService = FileServiceFactory.getFileService();
        AppEngineFile file = fileService.createNewBlobFile("images/png", testImageFile.getName());
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);

        OutputStream output = Channels.newOutputStream(writeChannel);
        output.write(imageBytes);
        output.close();
        writeChannel.closeFinally();
        BlobKey blobKey = fileService.getBlobKey(file);

        //ログイン状態の作成
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        int beforeCount = tester.count(Profile.class);

        tester.param("key", blobKey.getKeyString());
        tester.start(PATH);

        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat("jQuery pluginのためのContent-Typeはtext/html", tester.response.getContentType(), is("text/html"));
        assertThat("Character-Encodingはutf-8", tester.response.getCharacterEncoding(), is("utf-8"));
        Profile profile = ProfileMeta.get().jsonToModel(tester.response.getOutputAsString());
        assertThat("JSONにimageUrlのエントリが含まれる", profile.getProfileUrl(), is(notNullValue()));

        int afterCount = tester.count(Profile.class);
        assertThat("Profileが一件増える", afterCount, is(beforeCount + 1));

    }

    @Test
    public void ユーザのプロフィール画像の取得_画像を設定していないユーザ() throws Exception{
        tester.param("user", "uso800");
        tester.start(PATH);

        assertThat("ImagesControllerのインスタンスが使用される", tester.getController(), instanceOf(ImagesController.class));
        assertThat("ステータスコードが404", tester.response.getStatus(), is(HttpServletResponse.SC_NOT_FOUND));
    }

    @Test
    public void ユーザーのプロフィール画像の取得() throws Exception{
        //ユーザに対応するProfileを準備する
        Profile p = new Profile();
        p.setKey(Datastore.createKey(Profile.class, "1"));
        p.setProfileUrl("dummy_url");
        Datastore.put(p);

        tester.param("user", "1");
        tester.start(PATH);

        assertThat("ImagesControlerのインスタンスが使用される", tester.getController(), instanceOf(ImagesController.class));
        assertThat("リダイレクトが返される", tester.isRedirect(), is(true));
        assertThat("リダイレクト先が画像へのURL", tester.getDestinationPath(), is("dummy_url"));
    }
}
