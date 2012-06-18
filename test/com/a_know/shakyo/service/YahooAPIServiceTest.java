package com.a_know.shakyo.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;
import org.slim3.tester.URLFetchHandler;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class YahooAPIServiceTest extends AppEngineTestCase {

    private YahooAPIService service = new YahooAPIService();

    @Test
    public void 形態素解析APIで単語を抽出する() throws Exception {
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

        Set<String> parsed = YahooAPIService.parse("本日は晴天なり本日は晴天なり");

        assertThat("重複した単語は排除される", parsed.size(), is(2));
        Iterator<String> iterator = parsed.iterator();
        assertThat(iterator.next(), is("本日"));
        assertThat(iterator.next(), is("晴天"));
    }
}
