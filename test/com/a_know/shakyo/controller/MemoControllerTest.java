package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.TestEnvironment;

import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.model.Memo;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueAddRequest;
import com.google.apphosting.api.ApiProxy;

public class MemoControllerTest extends ControllerTestCase {

    static final String PATH = "/Memo";

    @Test
    public void 特定の議事録にメモを追加できる_ログインしていない() throws Exception {
        Key minutesKey = MinutesService.put("テスト用議事録１");

        tester.request.setMethod("POST");

        tester.start(PATH);
        assertThat("レスポンスコードが401", tester.response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
    }

    @Test
    public void 特定の議事録にメモを追加できる() throws Exception {
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        Key minutesKey = MinutesService.put("テスト用議事録１");

        tester.request.setMethod("POST");
        tester.param("minutes", Datastore.keyToString(minutesKey));
        tester.param("memo", "テスト用議事録１");

        tester.start(PATH);
        assertThat("MemoControllerのインスタンスが使用される", tester.getController(), instanceOf(MemoController.class));
        assertThat("レスポンスコードが204", tester.response.getStatus(), is(HttpServletResponse.SC_NO_CONTENT));
    }

    @Test
    public void 特定の議事録にメモを追加できる_memoを指定しない() throws Exception{
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        Key minutesKey = MinutesService.put("テスト用議事録１");

        tester.request.setMethod("POST");
        tester.param("minutes", Datastore.keyToString(minutesKey));

        tester.start(PATH);
        assertThat("MemoControllerのインスタンスが使用される", tester.getController(), instanceOf(MemoController.class));
        assertThat("レスポンスコードが400", tester.response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
    }

    @Test
    public void 特定の議事録にメモを追加できる_minutesを指定しない() throws Exception{
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        @SuppressWarnings("unused")
        Key minutesKey = MinutesService.put("テスト用議事録１");

        tester.request.setMethod("POST");
        tester.param("memo", "テスト用議事録１");

        tester.start(PATH);
        assertThat("MemoControllerのインスタンスが使用される", tester.getController(), instanceOf(MemoController.class));
        assertThat("レスポンスコードが400", tester.response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
    }
    @Test
    public void 特定の議事録にメモを追加できる_無効なminutesを指定する() throws Exception{
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        @SuppressWarnings("unused")
        Key minutesKey = MinutesService.put("テスト用議事録１");

        tester.request.setMethod("POST");
        tester.param("minutes", "test");
        tester.param("memo", "テスト用議事録１");

        tester.start(PATH);
        assertThat("MemoControllerのインスタンスが使用される", tester.getController(), instanceOf(MemoController.class));
        assertThat("レスポンスコードが400", tester.response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
    }
    @Test
    public void 議事録ごとに議事録に追加されたメモを古い順に一覧表示できる() throws Exception{
        Key minutesKey = MinutesService.put("テスト用議事録１");

        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < 5; i++){
            Memo memo = new Memo();
            memo.setMinutes(minutesKey);
            memo.setMemo("メモ" + i);
            memo.setCreatedAt(cal.getTime());
            Datastore.put(memo);
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }

        tester.request.setMethod("GET");
        tester.param("minutes", Datastore.keyToString(minutesKey));
        tester.start(PATH);


        assertThat("MemoControllerのインスタンスが使用される", tester.getController(), instanceOf(MemoController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat("Content-typeはapplication/json", tester.response.getContentType(), is("application/json"));
        assertThat("Character-Encodingはutf-8", tester.response.getCharacterEncoding(), is("utf-8"));

        String json = tester.response.getOutputAsString();
        Memo[] memos = MemoMeta.get().jsonToModels(json);

        assertThat("全ての議事録が返る", memos.length, is(5));
    }
    @Test
    public void 議事録ごとに議事録に追加されたメモを古い順に一覧表示できる_minutesを指定しない() throws Exception{
        tester.request.setMethod("GET");
        tester.start(PATH);

        assertThat("MemoControllerのインスタンスが使用される", tester.getController(), instanceOf(MemoController.class));
        assertThat("レスポンスコードが400", tester.response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
    }
    @Test
    public void 議事録ごとに議事録に追加されたメモを古い順に一覧表示できる_minutesに無効な値を指定する() throws Exception{
        tester.request.setMethod("GET");
        tester.param("minutes", "test");
        tester.start(PATH);

        assertThat("MemoControllerのインスタンスが使用される", tester.getController(), instanceOf(MemoController.class));
        assertThat("レスポンスコードが400", tester.response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
    }
    @Test
    public void GETリクエストのときにアクセスカウンタ用のタスクが追加される() throws Exception{
        Key minutesKey = MinutesService.put("テスト用議事録1");

        int before = tester.tasks.size();
        tester.param("minutes", Datastore.keyToString(minutesKey));
        tester.start(PATH);

        int after = tester.tasks.size();

        assertThat("Taskが一件追加される", after, is(before + 1));
        TaskQueueAddRequest task = tester.tasks.get(0);
        assertThat("access-logキューにTaskが追加される", task.getQueueName(), is("access-log"));
        assertThat("TaskにminutesKeyパラメータが設定される", task.getBody().startsWith("minutesKey="), is(true));
    }
}
