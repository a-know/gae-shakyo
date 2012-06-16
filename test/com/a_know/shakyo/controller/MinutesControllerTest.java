package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.TestEnvironment;

import com.a_know.shakyo.meta.MinutesMeta;
import com.a_know.shakyo.model.Minutes;
import com.a_know.shakyo.service.MemoService;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.mail.MailServicePb.MailMessage;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Delegate;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;

public class MinutesControllerTest extends ControllerTestCase {
    static final String PATH = "/Minutes";

    @Test
    public void 議事録のタイトルを作成できる_ログインしていない() throws Exception {
        tester.request.setMethod("POST");

        tester.start(PATH);
        assertThat("レスポンスコードが401", tester.response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));

    }
    @Test
    public void 議事録のタイトルを作成できる() throws Exception {
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        tester.request.setMethod("POST");
        tester.param("title", "テスト用議事録１");

        tester.start(PATH);
        assertThat("MinutesControllerのインスタンスが使用される", tester.getController(), instanceOf(MinutesController.class));
        assertThat("レスポンスコードが204", tester.response.getStatus(), is(HttpServletResponse.SC_NO_CONTENT));

    }

    @Test
    public void 議事録のタイトルを作成できる＿パラメータを指定しない() throws Exception{
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");
        tester.request.setMethod("POST");

        tester.start(PATH);
        assertThat("MinutesControllerのインスタンスが使用される", tester.getController(), instanceOf(MinutesController.class));
        assertThat("レスポンスコードが204", tester.response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
    }

    @Test
    public void 議事録の一覧を新しい順に表示できる() throws Exception{
        for(int i = 0; i < 5; i++){
            Minutes minutes = new Minutes();
            minutes.setTitle("テスト用議事録" + i);
            minutes.setCreatedAt(new Date());
            Datastore.put(minutes);
        }

        tester.request.setMethod("GET");

        tester.start(PATH);
        assertThat("MinutesControllerのインスタンスが使用される", tester.getController(), instanceOf(MinutesController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat("Content-typeはapplication/json", tester.response.getContentType(), is("application/json"));
        assertThat("Character-Encodingはutf-8", tester.response.getCharacterEncoding(), is("utf-8"));

        String json = tester.response.getOutputAsString();
        Minutes[] minutes = MinutesMeta.get().jsonToModels(json);
        assertThat("全ての議事録が返る", minutes.length, is(5));
    }

    @Test
    public void 議事録を追加するとメールが送信される() throws Exception{
        //ログイン状態の作成
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        //メール送信APIをフックするApiProxy.Delegateを適用しておく
        Delegate parentDelegate = ApiProxy.getDelegate();
        MailDelegate mailDelegate = new MailDelegate();
        ApiProxy.setDelegate(mailDelegate);

        tester.request.setMethod("POST");
        tester.param("title", "テスト用議事録１");

        tester.start(PATH);

        assertThat("メールが送信される", mailDelegate.messages.size(), is(1));
        MailMessage mail = mailDelegate.messages.get(0);
        assertThat("From", mail.getSender(), is("minutes@gae-shakyo.appspotmail.com"));
        String subject = mail.getTextBody(Charset.forName("utf-8"));
        assertThat("Subject", mail.getTextBody(Charset.forName("utf-8")), containsString("http://localhost/minutes.html?minutes="));

        //ApiProxy.Delegateを音に戻す
        ApiProxy.setDelegate(parentDelegate);
    }

    static class MailDelegate implements Delegate<Environment> {
        final Delegate<Environment> parent = ApiProxy.getDelegate();

        public void flushLogs(Environment e){
            parent.flushLogs(e);
        }

        public List<Thread> getRequestThreads(Environment e){
            return parent.getRequestThreads(e);
        }

        public void log(Environment e, LogRecord r){
            parent.log(e, r);
        }

        public Future<byte[]> makeAsyncCall(Environment e, String packageName, String methodName, byte[] request, ApiConfig apiConfig){
            inspectMailService(packageName, methodName, request);
            return parent.makeAsyncCall(e, packageName, methodName, request, apiConfig);
        }

        public byte[] makeSyncCall(Environment e, String packageName, String methodName, byte[] request) throws ApiProxyException{
            inspectMailService(packageName, methodName, request);
            return parent.makeSyncCall(e, packageName, methodName, request);
        }

        final List<MailMessage> messages = new ArrayList<MailMessage>();

        void inspectMailService(String packageName, String methodName, byte[] request){
            if((packageName.equals("mail") && methodName.equals("SendToAdmins")) || (packageName.equals("mail") && methodName.equals("Send"))){
                //mail#SendToAdmins RPCが実行された場合
                //バイト配列からProtocolBufferオブジェクトを組み立てなおし、messagesに追加する
                MailMessage messagePb = new MailMessage();
                messagePb.mergeFrom(request);
                messages.add(messagePb);
            }
        }
    }

    @Test
    public void 議事録のTSVをダウンロードする() throws Exception{
        //ログイン状態を作っておく
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        Key minutesKey = MinutesService.put("テスト用議事録");
        for(int i = 0; i < 5; i++){
            MemoService.put(minutesKey, "memo" + i);
        }

        Minutes minutes = Datastore.get(Minutes.class, minutesKey);
        BlobKey blobKey = MinutesService.exportAsTsv(minutes);

        tester.param("download", blobKey.getKeyString());
        tester.start(PATH);

        assertThat("MinutesControllerのインスタンスが使用される", tester.getController(), instanceOf(MinutesController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat("Content-TypeはTSV", tester.response.getContentType(), is("text/tab-separeted-values"));
        assertThat("Content-Lengthが指定されている", tester.response.getContentLength(), is(not(0)));
        assertThat("Content-dispositionヘッダにファイル名が指定されている", tester.response.getHeader("Content-disposition"), is("attachment; テスト用議事録"));
        assertThat("コンテントのサイズ=Content-length", tester.response.getOutputAsByteArray().length, is(tester.response.getContentLength()));
    }

    @Test
    public void 議事録の削除要求を受け付ける_ログインしていない() throws Exception {
        Key minutesKey = MinutesService.put("テスト用議事録");
        for(int i = 0; i < 5; i++){
            MemoService.put(minutesKey, "memo" + i);
        }

        tester.param("delete", Datastore.keyToString(minutesKey));
        tester.start(PATH);

        assertThat("MinutesControllerのインスタンスが使用される", tester.getController(), instanceOf(MinutesController.class));
        assertThat("レスポンスコードが401", tester.response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    public void 議事録の削除要求を受け付ける_議事録作成者以外が要求する() throws Exception{
        //ログイン状態の作成
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        Key minutesKey = MinutesService.put("テスト用議事録");
        for(int i = 0; i < 5; i++){
            MemoService.put(minutesKey, "memo" + i);
        }

        //作成者以外がログインした状態にする
        e.setEmail("other@example.com");

        tester.param("delete", Datastore.keyToString(minutesKey));
        tester.start(PATH);

        assertThat("MinutesControllerのインスタンスが使用される", tester.getController(), instanceOf(MinutesController.class));
        assertThat("レスポンスコードが403", tester.response.getStatus(), is(HttpServletResponse.SC_FORBIDDEN));
    }

    @Test
    public void 議事録の削除要求を受け付ける() throws Exception{
        //ログイン状態の作成
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        Key minutesKey = MinutesService.put("テスト用議事録");
        for(int i = 0; i < 5; i++){
            MemoService.put(minutesKey, "memo" + i);
        }
        Minutes minutes = Datastore.get(Minutes.class, minutesKey);

        //Delegateを適用
        Delegate parentDelegate = ApiProxy.getDelegate();
        MailDelegate mailDelegate = new MailDelegate();
        ApiProxy.setDelegate(mailDelegate);

        tester.param("delete", Datastore.keyToString(minutesKey));
        tester.start(PATH);

        assertThat("MinutesControllerのインスタンスが使用される", tester.getController(), instanceOf(MinutesController.class));
        assertThat("レスポンスコードが204", tester.response.getStatus(), is(HttpServletResponse.SC_NO_CONTENT));

        assertThat("メールが送信される", mailDelegate.messages.size(), is(1));
        MailMessage mail = mailDelegate.messages.get(0);
        assertThat("From", mail.getSender(), is("minutes@gae-shakyo.appspotmail.com"));
        assertThat("To", mail.getTo(0), is("test@example.com"));
        assertThat("Subject", mail.getSubject(), is("議事録[" + minutes.getTitle() + "]がTSVに変換されました"));
        assertThat("Body", mail.getTextBody(Charset.forName("utf-8")), containsString("http://localhost/minutes?download="));

        //Delegateをもとに戻す
        ApiProxy.setDelegate(parentDelegate);
    }
}
