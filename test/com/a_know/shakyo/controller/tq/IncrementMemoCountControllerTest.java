package com.a_know.shakyo.controller.tq;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.a_know.shakyo.model.Minutes;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.datastore.Key;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class IncrementMemoCountControllerTest extends ControllerTestCase {

    static final String PATH = "/tq/IncrementMemoCount";

    @Test
    public void タスクを実行する() throws Exception {
        Key minutesKey = MinutesService.put("テスト用議事録１");
        Minutes before = Datastore.get(Minutes.class, minutesKey);

        tester.request.addParameter("minutesKey", Datastore.keyToString(minutesKey));
        tester.start(PATH);

        assertThat("IncrementMemoCountControllerのインスタンスが使用される", tester.getController(), instanceOf(IncrementMemoCountController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));

        Minutes after = Datastore.get(Minutes.class, minutesKey);
        assertThat("メモ件数が１件増える", after.getMemoCount(), is(before.getMemoCount() + 1));
    }

}
