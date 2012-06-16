package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.a_know.shakyo.meta.MinutesChannelMeta;
import com.a_know.shakyo.model.MinutesChannel;
import com.a_know.shakyo.service.MemoService;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.channel.ChannelServicePb;
import com.google.appengine.api.datastore.Key;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Delegate;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;

public class ChannelControllerTest extends ControllerTestCase {

    static final String PATH = "/Channel";
    @Test
    public void 新しいチャンネルを作成できる() throws Exception {
        //テスト用の議事録
        Key testMinutesKey = MinutesService.put("テスト用議事録１");

        tester.request.setMethod("GET");
        tester.param("minutes", Datastore.keyToString(testMinutesKey));
        tester.start(PATH);

        assertThat("ChannelControllerのインスタンスが使用される", tester.getController(), instanceOf(ChannelController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));

        MinutesChannel minutesChannel = MinutesChannelMeta.get().jsonToModel(tester.response.getOutputAsString());
        assertThat("channelのtokenが返される", minutesChannel.getToken(), is(notNullValue()));
    }

    @Test
    public void 接続済みのチャンネルにプッシュできる(){
        //テスト用の議事録
        Key testMinutesKey = MinutesService.put("テスト用議事録１");

        //テスト用の接続済みチャンネル１
        MinutesChannel c1 = new MinutesChannel();
        c1.setMinutesKey(testMinutesKey);
        c1.setToken("test-token1");
        c1.setKey(Datastore.createKey(MinutesChannel.class, "test-client-id1"));
        Datastore.put(c1);

        //テスト用の接続済みチャンネル２
        MinutesChannel c2 = new MinutesChannel();
        c2.setMinutesKey(testMinutesKey);
        c2.setToken("test-token2");
        c2.setKey(Datastore.createKey(MinutesChannel.class, "test-client-id2"));
        Datastore.put(c2);

        //新たに投稿されてプッシュされるべき投稿
        Key memoKey = MemoService.put(testMinutesKey, "test投稿");

        ChannelDelegate channelDelegate = new ChannelDelegate();
        ApiProxy.setDelegate(channelDelegate);
        try{
            ChannelController.pushMemo(memoKey);
        }finally{
            ApiProxy.setDelegate(channelDelegate.parent);
        }
        assertThat("接続済みのチャンネルにプッシュされる", channelDelegate.sendMessageRequests.size(), is(2));
        assertThat(channelDelegate.sendMessageRequests.get(0).getApplicationKey(), is("test-client-id1"));
        assertThat(channelDelegate.sendMessageRequests.get(1).getApplicationKey(), is("test-client-id2"));
    }

    @Test
    public void 接続済みのチャンネルにプッシュできる_関係ないチャンネルにはプッシュされない(){
        //テスト用の議事録
        Key testMinutesKey = MinutesService.put("テスト用議事録１");

        //テスト用の接続済みチャンネル１
        MinutesChannel c1 = new MinutesChannel();
        c1.setMinutesKey(testMinutesKey);
        c1.setToken("test-token1");
        c1.setKey(Datastore.createKey(MinutesChannel.class, "test-client-id1"));
        Datastore.put(c1);

        //新たに投稿されてプッシュされるべき投稿
        Key memoKey = MemoService.put(testMinutesKey, "test投稿");

        //テスト用の議事録２（プッシュとは関係ない議事録）
        Key testMinutesKey2 = MinutesService.put("テスト用議事録２");

        //テスト用の接続済みチャンネル２（プッシュとは関係ないチャネル）
        MinutesChannel c2 = new MinutesChannel();
        c2.setMinutesKey(testMinutesKey2);
        c2.setToken("test-token2");
        c2.setKey(Datastore.createKey(MinutesChannel.class, "test-client-id2"));
        Datastore.put(c2);

        ChannelDelegate channelDelegate = new ChannelDelegate();
        ApiProxy.setDelegate(channelDelegate);
        try{
            ChannelController.pushMemo(memoKey);
        }finally{
            ApiProxy.setDelegate(channelDelegate.parent);
        }

        assertThat("接続済みのチャンネルにのみプッシュされる", channelDelegate.sendMessageRequests.size(), is(1));
        assertThat(channelDelegate.sendMessageRequests.get(0).getApplicationKey(), is("test-client-id1"));
    }

    static class ChannelDelegate implements Delegate<Environment> {

        final Delegate<Environment> parent = ApiProxy.getDelegate();

        public void flushLogs(Environment arg0) {
            parent.flushLogs(arg0);
        }

        public List<Thread> getRequestThreads(Environment arg0) {
            return parent.getRequestThreads(arg0);
        }

        public void log(Environment arg0, LogRecord arg1) {
            parent.log(arg0, arg1);
        }

        public Future<byte[]> makeAsyncCall(Environment arg0, String arg1,
                String arg2, byte[] arg3, ApiConfig arg4) {
            inspectChannelService(arg1, arg2, arg3);
            return parent.makeAsyncCall(arg0, arg1, arg2, arg3, arg4);
        }

        public byte[] makeSyncCall(Environment arg0, String arg1, String arg2,
                byte[] arg3) throws ApiProxyException {
            inspectChannelService(arg1, arg2, arg3);
            return parent.makeSyncCall(arg0, arg1, arg2, arg3);
        }

        final List<ChannelServicePb.CreateChannelRequest> createChannelRequests = new ArrayList<ChannelServicePb.CreateChannelRequest>();
        final List<ChannelServicePb.SendMessageRequest> sendMessageRequests = new ArrayList<ChannelServicePb.SendMessageRequest>();

        void inspectChannelService(String packageName, String methodName, byte[] request){
            if(packageName.equals("channel") && methodName.equals("CreateChannel")){
                ChannelServicePb.CreateChannelRequest reqPb = new ChannelServicePb.CreateChannelRequest();
                reqPb.mergeFrom(request);
                createChannelRequests.add(reqPb);
            }else if(packageName.equals("channel") && methodName.equals("SendChannelMessage")){
                ChannelServicePb.SendMessageRequest reqPb = new ChannelServicePb.SendMessageRequest();
                reqPb.mergeFrom(request);
                sendMessageRequests.add(reqPb);
            }
        }

    }
}
