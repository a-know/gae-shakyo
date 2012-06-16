package com.a_know.shakyo.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.Date;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;

import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.meta.MinutesMeta;
import com.a_know.shakyo.model.Memo;
import com.a_know.shakyo.model.Minutes;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.prospectivesearch.ProspectiveSearchServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;


public class MinutesService {

    static final MinutesMeta meta = MinutesMeta.get();
    static final String MEMCACHE_KEY_LIST = "LIST_OF_MINUTES";

    public static Key put(String string) {
        Minutes minutes = new Minutes();
        minutes.setTitle(string);
        minutes.setCreatedAt(new Date());
        minutes.setAuthor(UserServiceFactory.getUserService().getCurrentUser());
        Datastore.put(minutes);
        Memcache.delete(MEMCACHE_KEY_LIST);
        return minutes.getKey();
    }

    public static List<Minutes> list() {
        List<Minutes> list = Memcache.get(MEMCACHE_KEY_LIST);
        if(list != null){
            return list;
        }
        list = Datastore.query(meta).sort(meta.createdAt.desc).asList();
        Memcache.put(MEMCACHE_KEY_LIST, list);
        return list;
    }

    public static void incrementMemoCount(Key minutesKey){
        Transaction tx = Datastore.beginTransaction();

        try{
            Minutes minutes = Datastore.get(tx, meta, minutesKey);
            minutes.setMemoCount(minutes.getMemoCount() + 1);
            minutes.setUpdateAt(new Date());

            ProspectiveSearchServiceFactory.getProspectiveSearchService().match(meta.modelToEntity(minutes), "HotMinutes");

            Datastore.put(tx, minutes);
            tx.commit();
        }finally{
            if(tx.isActive()){
                tx.rollback();
            }
        }
        Memcache.delete(MEMCACHE_KEY_LIST);
    }

    public static List<Key> queryForUpdateMemoCount(){
        long now = new Date().getTime();
        Date before24hours = new Date(now - 24 * 60 * 60 * 1000);
        Date before48hours = new Date(now - 48 * 60 * 60 * 1000);
        return Datastore.query(meta).filter(meta.updateAt.lessThan(before24hours)).filter(meta.updateAt.lessThan(before48hours)).asKeyList();
    }

    public static void updateMemoCount(Key minutesKey){
        MemoMeta memoMeta = MemoMeta.get();
        int memoCount = Datastore.query(memoMeta).filter(memoMeta.minutes.equal(minutesKey)).count();
        Minutes minutes = Datastore.get(meta, minutesKey);
        minutes.setMemoCount(memoCount);
        Datastore.put(minutes);
        Memcache.delete(MEMCACHE_KEY_LIST);
    }

    public static void deleteMinutes(Minutes minutes){
        Key minutesKey = minutes.getKey();
        Memcache.delete(minutesKey);
        Memcache.delete(MEMCACHE_KEY_LIST);

        List<Key> deleteKeyList = Datastore.query(Memo.class).filter(MemoMeta.get().minutes.equal(minutesKey)).asKeyList();

        Datastore.delete(deleteKeyList);
        Datastore.delete(minutesKey);
    }

    public static BlobKey exportAsTsv(Minutes minutes) throws IOException{
        FileService fileService = FileServiceFactory.getFileService();
        AppEngineFile file = fileService.createNewBlobFile("text/tab-separeted-values", minutes.getTitle());
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
        PrintWriter w = new PrintWriter(Channels.newWriter(writeChannel, "utf-8"));

        List<Memo> list = MemoService.list(minutes.getKey());
        for(Memo m : list){
            w.print("\"");
            w.print(m.getCreatedAt());
            w.print("\"");
            w.print("\t");
            w.print("\"");
            w.print(m.getAuthor().getEmail());
            w.print("\"");
            w.print("\t");
            w.print("\"");
            w.print(m.getMemo());
            w.print("\"");
            w.println();
        }

        w.close();
        writeChannel.closeFinally();

        return fileService.getBlobKey(file);
    }

}
