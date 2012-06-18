package com.a_know.shakyo.controller.tq;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.URLFetchHandler;

import com.a_know.shakyo.model.KeyWord;
import com.a_know.shakyo.service.MemoService;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class YahooControllerTest extends ControllerTestCase {

    static final String PATH = "/tq/Yahoo";

    @Test
    public void 投稿からKeyWordエンティティを作成して保存する() throws Exception {
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

        int beforeCount = tester.count(KeyWord.class);
        Key minutesKey = MinutesService.put("テスト用議事録");
        Key memoKey = MemoService.put(minutesKey, "本日は晴天なり本日は晴天なり");

        tester.param("memoKey", Datastore.keyToString(memoKey));
        tester.start(PATH);

        assertThat("YahooControllerのインスタンスが使用される", tester.getController(), instanceOf(YahooController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));

        int afterCount = tester.count(KeyWord.class);
        assertThat("KeyWordエンティティが一件増える", afterCount, is(beforeCount + 1));
    }
}
