package com.a_know.shakyo.service;

import java.util.Date;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;

import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.model.Memo;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.UserServiceFactory;


public class MemoService {

    static final MemoMeta meta = new MemoMeta();

    public static Key put(Key minutesKey, String memoString) {
        Memo memo = new Memo();
        memo.setMinutes(minutesKey);
        memo.setMemo(memoString);
        memo.setCreatedAt(new Date());
        memo.setAuthor(UserServiceFactory.getUserService().getCurrentUser());
        Datastore.put(memo);

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/tq/IncrementMemoCount").param("minutesKey", Datastore.keyToString(minutesKey)));
        QueueFactory.getQueue("parse").add(TaskOptions.Builder.withUrl("/tq/Yahoo").param("memoKey", Datastore.keyToString(memo.getKey())));

        Memcache.delete(minutesKey);

        return memo.getKey();
    }

    public static List<Memo> list(Key minutesKey) {
        List<Memo> list = Memcache.get(minutesKey);
        if(list != null){
            return list;
        }
        list = Datastore.query(meta).filter(meta.minutes.equal(minutesKey)).sort(meta.createdAt.asc).asList();
        Memcache.put(minutesKey, list);
        return list;
    }

}
