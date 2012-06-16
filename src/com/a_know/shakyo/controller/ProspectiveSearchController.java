package com.a_know.shakyo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.prospectivesearch.FieldType;
import com.google.appengine.api.prospectivesearch.ProspectiveSearchService;
import com.google.appengine.api.prospectivesearch.ProspectiveSearchServiceFactory;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

public class ProspectiveSearchController extends Controller {

    static final Logger logger = Logger.getLogger(ProspectiveSearchController.class.getName());
    static final String ADMIN_EMAIL = "a.know.3373@gmail.com";

    @Override
    public Navigation run() throws Exception {
        if(isPost()){
            doPost();
        }else{
            doGet();
        }
        return null;
    }

    private Navigation doPost(){
        ProspectiveSearchService s = ProspectiveSearchServiceFactory.getProspectiveSearchService();
        Entity document = s.getDocument(request);
        logger.info("Topic:)" + asString("topic") + ", id=" + asString("id"));
        logger.info(document.toString());
        sendMessage(document);
        return null;
    }

    private Navigation doGet(){
        ProspectiveSearchService ps = ProspectiveSearchServiceFactory.getProspectiveSearchService();
        String topic = "HotMinutes";
        long sec = 0;

        Map<String, FieldType> schema = new HashMap<String, FieldType>();
        schema.put("memoCount", FieldType.INT32);

        ps.subscribe(topic, "HotMinutes_5", sec, "memoCount = 5", schema);
        ps.subscribe(topic, "HotMinutes_10", sec, "memoCount = 10", schema);
        ps.subscribe(topic, "HotMinutes_15", sec, "memoCount = 15", schema);


        XMPPServiceFactory.getXMPPService().sendInvitation(new JID(ADMIN_EMAIL));

        return null;
    }

    static void sendMessage(Entity document){
        StringBuilder b = new StringBuilder();
        b.append("議事録'");
        b.append(document.getProperty("title"));
        b.append("'に");
        b.append(document.getProperty("memoCount"));
        b.append("件目が投稿されました");

        Message m = new MessageBuilder().withRecipientJids(new JID(ADMIN_EMAIL)).withBody(b.toString()).build();
        XMPPServiceFactory.getXMPPService().sendMessage(m);
    }
}
