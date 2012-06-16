package com.a_know.shakyo.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;
import org.slim3.tester.AppEngineTestCase;
import org.slim3.tester.TestEnvironment;

import com.a_know.shakyo.model.Memo;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueAddRequest;
import com.google.apphosting.api.ApiProxy;

public class MemoServiceTest extends AppEngineTestCase {

    private MemoService service = new MemoService();

    @Test
    public void 特定の議事録にメモを追加できる() throws Exception {
        // test@example.comというユーザーがログイン中、という状態を作る
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");
        Key minutesKey = MinutesService.put("テスト用議事録１");

        int before = tester.count(Memo.class);
        Key key = MemoService.put(minutesKey, "メモ１");
        int after = tester.count(Memo.class);
        assertThat("Memoが１件増える", after, is(before + 1));
        Memo memo = Datastore.get(Memo.class, key);

        assertThat(memo, is(notNullValue()));
        assertThat("議事録への参照が設定される", memo.getMinutes(), is(minutesKey));
        assertThat("memoが設定される", memo.getMemo(), is("メモ１"));
        assertThat("createdAtが設定される", memo.getCreatedAt(), is(notNullValue()));
        assertThat("authorが設定される", memo.getAuthor(), is(notNullValue()));
    }

    @Test
    public void 議事録ごとに議事録に追加されたメモを古い順に一覧表示できる(){
        Key minutesKey = MinutesService.put("テスト用議事録１");

        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < 5; i++){
            Memo memo = new Memo();
            memo.setMinutes(minutesKey);
            memo.setMemo("メモ" + 1);
            memo.setCreatedAt(cal.getTime());
            Datastore.put(memo);
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }

        int count = 0;
        Date before = null;
        List<Memo> list = MemoService.list(minutesKey);
        for(Memo m : list){
            Date createdAt = m.getCreatedAt();
            if(before != null){
                assertThat("古いものから取得できている", createdAt.compareTo(before) > 0, is(true));
            }
            before = createdAt;
            count++;
        }
        assertThat("全てのエンティティが取得できている", count, is(5));
    }

    @Test
    public void 議事録のメモの一覧を取得する際にキャッシュを利用する(){
        Key minutesKey = MinutesService.put("テスト用議事録");

        long before = Memcache.statistics().getHitCount();
        MemoService.list(minutesKey);
        long after1 = Memcache.statistics().getHitCount();
        assertThat("一回目はキャッシュが効かない", after1, is(before));
        MemoService.list(minutesKey);
        long after2 = Memcache.statistics().getHitCount();
        assertThat("二回目はキャッシュが効く", after2, is(before + 1));
    }

    @Test
    public void 議事録のメモ追加後にキャッシュが削除される(){
        Key minutesKey = MinutesService.put("テスト用議事録１");

        Memcache.put(minutesKey, "dummy");
        assertThat(Memcache.contains(minutesKey), is(true));
        MemoService.put(minutesKey, "メモ１");
        assertThat(Memcache.contains(minutesKey), is(false));
    }

    @Test
    public void 議事録のメモ追加後にメモ件数を加算するタスクが追加される(){
        Key minutesKey = MinutesService.put("テスト用議事録１");

        int before = tester.tasks.size();
        MemoService.put(minutesKey, "メモ１");
        int after = tester.tasks.size();
        assertThat("タスクが１件増える", after, is(before + 1));
        TaskQueueAddRequest task = tester.tasks.get(0);
        assertThat(task.getUrl(), is("/tq/IncrementMemoCount"));
        assertThat(task.getBody(), is("minutesKey=" + Datastore.keyToString(minutesKey)));
    }
}
