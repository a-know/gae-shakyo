package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;

import com.a_know.shakyo.meta.MinutesMeta;
import com.a_know.shakyo.model.Minutes;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.prospectivesearch.ProspectiveSearchService;
import com.google.appengine.api.prospectivesearch.ProspectiveSearchServiceFactory;
import com.google.appengine.api.prospectivesearch.Subscription;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.appengine.api.xmpp.XMPPServicePb;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Delegate;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;

public class ProspectiveSearchControllerTest extends ControllerTestCase {

    static final String PATH = "/prospectiveSearch";
    XMPPDelegate delegate;

    @Override
    public void setUp() throws Exception{
        super.setUp();
        delegate = new XMPPDelegate();
        ApiProxy.setDelegate(delegate);
    }

    @Override
    public void tearDown() throws Exception{
        ApiProxy.setDelegate(delegate.parent);
        super.tearDown();
    }

    static class XMPPDelegate implements Delegate<Environment>{

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
            return parent.makeAsyncCall(arg0, arg1, arg2, arg3, arg4);
        }

        final List<XMPPServicePb.XmppMessageRequest> messages = new ArrayList<XMPPServicePb.XmppMessageRequest>();
        final List<XMPPServicePb.XmppInviteRequest> invitations = new ArrayList<XMPPServicePb.XmppInviteRequest>();

        public byte[] makeSyncCall(Environment arg0, String arg1, String arg2,
                byte[] arg3) throws ApiProxyException {
            if(arg1.equals("xmpp") && arg2.equals("SendMessage")){
                XMPPServicePb.XmppMessageRequest requestPb = new XMPPServicePb.XmppMessageRequest();
                requestPb.mergeFrom(arg3);
                messages.add(requestPb);
            }else if(arg1.equals("xmpp") && arg2.equals("SendInvite")){
                XMPPServicePb.XmppInviteRequest requestPb = new XMPPServicePb.XmppInviteRequest();
                requestPb.mergeFrom(arg3);
                invitations.add(requestPb);
            }
            return parent.makeSyncCall(arg0, arg1, arg2, arg3);
        }

    }

    @Test
    public void HotMinutesトピックにクエリが登録される() throws Exception {
        tester.start(PATH);

        ProspectiveSearchService service = ProspectiveSearchServiceFactory.getProspectiveSearchService();
        List<Subscription> subs = service.listSubscriptions("HotMinutes");
        assertThat("三段階のクエリが登録される", subs.size(), is(3));

        for(Subscription sub : subs){
            assertThat("memoCount = [5|10|15]", sub.getQuery().matches("memoCount = (5|10|15)"), is(true));
        }
    }

    @Test
    public void XMPP招待が送信される() throws Exception{
        tester.start(PATH);

        assertThat("招待が一件送信される", delegate.invitations.size(), is(1));
        assertThat("宛先", delegate.invitations.get(0).getJid(), is(ProspectiveSearchController.ADMIN_EMAIL));
    }

    @Test
    public void XMPPメッセージが送信される() {
        Minutes minutes = new Minutes();
        minutes.setTitle("活発な議事録");
        minutes.setMemoCount(100);

        Entity document =MinutesMeta.get().modelToEntity(minutes);

        ProspectiveSearchController.sendMessage(document);

        assertThat("メッセージが一件送信される", delegate.messages.size(), is(1));
        assertThat("宛先", delegate.messages.get(0).getJid(0), is(ProspectiveSearchController.ADMIN_EMAIL));
        assertThat("指定した宛先に送られる", delegate.messages.get(0).getBody(), is("議事録'活発な議事録'に100件目が投稿されました"));
        XMPPServiceFactory.getXMPPService().getPresence(new JID(ProspectiveSearchController.ADMIN_EMAIL));
    }
}
