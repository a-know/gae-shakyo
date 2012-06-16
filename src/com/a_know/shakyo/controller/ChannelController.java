package com.a_know.shakyo.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.meta.MinutesChannelMeta;
import com.a_know.shakyo.model.Memo;
import com.a_know.shakyo.model.MinutesChannel;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Key;

public class ChannelController extends Controller {

    @Override
    public Navigation run() throws Exception {

        Key minutesKey = asKey("minutes");
        try{
            if(minutesKey == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
        }catch(IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        String clientId = UUID.randomUUID().toString();//【参考】ユニークな文字列をランダムに生成する方法
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String token = channelService.createChannel(clientId);

        MinutesChannel minutesChannel = new MinutesChannel();
        minutesChannel.setKey(Datastore.createKey(MinutesChannel.class, clientId));//【参考】特定の文字列からKeyを生成する方法
        minutesChannel.setCreatedAt(new Date());
        minutesChannel.setMinutesKey(minutesKey);
        minutesChannel.setToken(token);
        Datastore.putAsync(minutesChannel);

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().println(MinutesChannelMeta.get().modelToJson(minutesChannel));
        response.flushBuffer();

        return null;
    }

    static void pushMemo(Key memoKey){
        Memo memo = Datastore.getOrNull(Memo.class, memoKey);
        if(memo == null){
            return;
        }

        List<Key> channels = Datastore.query(MinutesChannel.class).filter(MinutesChannelMeta.get().minutesKey.equal(memo.getMinutes())).asKeyList();
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String memoJson = MemoMeta.get().modelToJson(memo);

        for(Key channel : channels){
            channelService.sendMessage(new ChannelMessage(channel.getName(), memoJson));//【参考】特定の文字列からキーを作成した際に、キーからその文字列を取り出す際の操作
        }
    }
}
