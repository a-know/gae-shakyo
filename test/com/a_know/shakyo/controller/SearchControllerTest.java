package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.URLFetchHandler;

import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.model.KeyWord;
import com.a_know.shakyo.model.Memo;
import com.a_know.shakyo.service.MemoService;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class SearchControllerTest extends ControllerTestCase {

    static final String PATH = "/search";

    @Test
    public void キーワードで検索する() throws Exception {
        tester.setUrlFetchHandler(new URLFetchHandler() {

            public int getStatusCode(URLFetchRequest req) throws IOException {
                return 200;
            }

            public byte[] getContent(URLFetchRequest request)
                    throws IOException {
                File testXmlFile = new File("test/yahooapi.xml");
                byte[] contents = new byte[(int) testXmlFile.length()];
                DataInputStream input = new DataInputStream(new FileInputStream(testXmlFile));
                input.readFully(contents);
                input.close();
                return contents;
            }
        });

        Key memoKey1 = createTestData();

        tester.param("query", "本日 晴天");
        tester.start(PATH);

        assertThat("SearhControllerのインスタンスが使用される", tester.getController(), instanceOf(SearchController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));

        Memo[] memos = MemoMeta.get().jsonToModels(tester.response.getOutputAsString());
        assertThat("「本日 晴天」にマッチするのは一件だけ", memos.length, is(1));
        assertThat("本日は晴天なり、がマッチする", memos[0].getKey(), is(memoKey1));
    }

    private Key createTestData(){
        Key minutesKey = MinutesService.put("テスト用議事録１");
        HashSet<String> keyWords = new HashSet<String>();

        Key memoKey1 = MemoService.put(minutesKey, "本日は晴天なり");
        KeyWord keyWord1 = new KeyWord();
        keyWord1.setKey(Datastore.createKey(memoKey1, KeyWord.class, "1"));
        keyWords.add("本日");
        keyWords.add("晴天");
        keyWord1.setWords(keyWords);
        Datastore.put(keyWord1);

        Key memoKey2 = MemoService.put(minutesKey, "本日は雨天なり");
        keyWords.clear();
        KeyWord keyWord2 = new KeyWord();
        keyWord2.setKey(Datastore.createKey(memoKey2, KeyWord.class, "1"));
        keyWords.add("本日");
        keyWords.add("雨天");
        keyWord2.setWords(keyWords);
        Datastore.put(keyWord2);

        Key memoKey3 = MemoService.put(minutesKey, "明日も雨天なり");
        keyWords.clear();
        KeyWord keyWord3 = new KeyWord();
        keyWord3.setKey(Datastore.createKey(memoKey3, KeyWord.class, "1"));
        keyWords.add("明日");
        keyWords.add("雨天");
        keyWord3.setWords(keyWords);
        Datastore.put(keyWord3);

        return memoKey1;
    }
}
