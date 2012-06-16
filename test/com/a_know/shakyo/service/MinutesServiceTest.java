package com.a_know.shakyo.service;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;
import org.slim3.tester.AppEngineTestCase;
import org.slim3.tester.TestEnvironment;

import com.a_know.shakyo.model.Minutes;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.prospectivesearch.ProspectiveSearchPb;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Delegate;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;

public class MinutesServiceTest extends AppEngineTestCase {

    private MinutesService service = new MinutesService();
    ProspectiveSearchDelegate delegate;

    @Override
    public void setUp() throws Exception{
        super.setUp();
        delegate = new ProspectiveSearchDelegate();
        ApiProxy.setDelegate(delegate);
    }

    @Override
    public void tearDown() throws Exception{
        ApiProxy.setDelegate(delegate.parent);
        super.tearDown();
    }

    static class ProspectiveSearchDelegate implements Delegate<Environment>{
        @SuppressWarnings("unchecked")
        final Delegate<Environment> parent = ApiProxy.getDelegate();

        public void flushLogs(Environment e){
            parent.flushLogs(e);
        }

        public List<Thread> getRequestThreads(Environment e){
            return parent.getRequestThreads(e);
        }

        public void log(Environment e, LogRecord r){
            parent.log(e, r);
        }

        public Future<byte[]> makeAsyncCall(Environment arg0, String arg1,
                String arg2, byte[] arg3, ApiConfig arg4) {
            return parent.makeAsyncCall(arg0, arg1, arg2, arg3, arg4);
        }

        boolean matchWasExecuted = false;

        public byte[] makeSyncCall(Environment arg0, String arg1, String arg2,
                byte[] arg3) throws ApiProxyException {
            if(arg1.equals("matcher") && arg2.equals("Match")){
                matchWasExecuted = true;
                ProspectiveSearchPb.MatchResponse resPb = new ProspectiveSearchPb.MatchResponse();
                return resPb.toByteArray();
            }else{
                return parent.makeSyncCall(arg0, arg1, arg2, arg3);
            }
        }
    }

    @Test
    public void 議事録のタイトルを作成できる() throws Exception {
        // test@example.comというユーザーがログイン中、という状態を作る
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        //テスト対象を実行前のMinutesの件数を保存
        int before = tester.count(Minutes.class);
        //テスト対象を実行
        Key key = MinutesService.put("テスト用議事録");
        //件数が1件増えたことを確認する。
        int after = tester.count(Minutes.class);
        assertThat("Minutesが1件増える", after, is(before + 1));
        //保存されたMinutesのtitle, createdAtに値が格納されていることを確認する。
        Minutes minutes = Datastore.get(Minutes.class, key);
        assertThat(minutes, is(notNullValue()));
        assertThat("titleが設定される", minutes.getTitle(), is("テスト用議事録"));
        assertThat("createdAtが設定される", minutes.getCreatedAt(), is(notNullValue()));
        assertThat("authorが設定される", minutes.getAuthor(), is(notNullValue()));
    }

    @Test
    public void 議事録の一覧を新しい順に表示できる(){
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < 5; i++){
            Minutes minutes = new Minutes();
            minutes.setTitle("テスト用議事録" + i);
            minutes.setCreatedAt(cal.getTime());
            Datastore.put(minutes);
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }

        Date before = null;
        List<Minutes> list = MinutesService.list();
        for(Minutes m : list){
            Date createdAt = m.getCreatedAt();
            if(before != null){
                assertThat("新しいものから取得できている", before.compareTo(createdAt) > 0, is(true));
            }
            before = createdAt;
        }
        assertThat("すべてのエンティティが取得できている", list.size(), is(5));
    }

    @Test
    public void 議事録の一覧を取得する際にキャッシュを利用する(){
        long before = Memcache.statistics().getHitCount();//キャッシュにヒットした回数・・・0のはず
        MinutesService.list();
        long after1 = Memcache.statistics().getHitCount();
        assertThat("一回目はキャッシュが効かない", after1, is(before));
        MinutesService.list();
        long after2 = Memcache.statistics().getHitCount();
        assertThat("二回目はキャッシュが効く", after2, is(before + 1));
    }

    @Test
    public void 議事録登録後にキャッシュが削除される(){
        Memcache.put(MinutesService.MEMCACHE_KEY_LIST, "dummy");
        assertThat("キャッシュが存在している", Memcache.contains(MinutesService.MEMCACHE_KEY_LIST), is(true));
        MinutesService.put("テスト用議事録");
        assertThat("キャッシュが消えている", Memcache.contains(MinutesService.MEMCACHE_KEY_LIST), is(false));
    }

    @Test
    public void メモ件数を加算する(){
        Key minutesKey = MinutesService.put("テスト用議事録１");

        Memcache.put(MinutesService.MEMCACHE_KEY_LIST, "dummy");
        Minutes before = Datastore.get(Minutes.class, minutesKey);
        MinutesService.incrementMemoCount(minutesKey);
        Minutes after = Datastore.get(Minutes.class, minutesKey);
        assertThat("メモ件数が１件増える", after.getMemoCount(), is(before.getMemoCount() + 1));
        assertThat("更新日時が設定される", after.getUpdateAt(), is(notNullValue()));
        assertThat("議事録一覧のキャッシュがクリアされる", Memcache.contains(MinutesService.MEMCACHE_KEY_LIST), is(false));
    }

    @Test
    public void メモ件数を数えて保存するための更新対象を抽出する(){
        List<Key> minutesKeys = new ArrayList<Key>();
        Calendar calendar = Calendar.getInstance();
        for(int i = 0; i < 6; i++){
            Minutes minutes = new Minutes();
            minutes.setTitle("テスト用議事録" + i);
            minutes.setCreatedAt(calendar.getTime());
            minutes.setUpdateAt(calendar.getTime());
            Datastore.put(minutes);
            minutesKeys.add(minutes.getKey());
            calendar.add(Calendar.HOUR_OF_DAY, -12);//12時間戻す
        }

        //0:現在  1:-12h  2:-24h  3:-36h  4:-48h  5:-60h
        //2,3が対象となるべき。
        List<Key> list = MinutesService.queryForUpdateMemoCount();
        assertThat("２つの議事録が返る", list.size(), is(2));
    }

    @Test
    public void 議事録のエンティティにメモエンティテイの数を保存する(){
        Key minutesKey = MinutesService.put("テスト用議事録");
        for(int i = 0; i < 5; i++){
            MemoService.put(minutesKey, "memo" + i);
        }
        Memcache.put(MinutesService.MEMCACHE_KEY_LIST, "dummy");
        Minutes before = Datastore.get(Minutes.class, minutesKey);

        assertThat("実行前のメモ数は0", before.getMemoCount(), is(0));
        MinutesService.updateMemoCount(minutesKey);

        Minutes after = Datastore.get(Minutes.class, minutesKey);
        assertThat("メモ数が5になる", after.getMemoCount(), is(5));
        assertThat("議事録一覧のキャッシュがクリアされる", Memcache.contains(MinutesService.MEMCACHE_KEY_LIST), is(false));
    }

    @Test
    public void 議事録を削除する(){
        Key minutesKey = MinutesService.put("テスト用議事録");
        for(int i = 0 ; i < 5; i++){
            MemoService.put(minutesKey, "memo" + i);
        }
        assertThat("実行前のメモ数は5", MemoService.list(minutesKey).size(), is(5));

        int beforeMinutesCount = MinutesService.list().size();

        Minutes minutes = Datastore.get(Minutes.class, minutesKey);
        MinutesService.deleteMinutes(minutes);

        assertThat("メモが全て削除される", MemoService.list(minutesKey).size(), is(0));
        assertThat("議事録が削除される", Datastore.getOrNull(minutesKey), is(nullValue()));
        assertThat("議事録の一覧が一件減っている", MinutesService.list().size(), is(beforeMinutesCount - 1));
    }

    @Test
    public void 議事録をTSVに変換する() throws IOException{
        //ログイン状態の作成
        TestEnvironment e = (TestEnvironment) ApiProxy.getCurrentEnvironment();
        e.setEmail("test@example.com");

        Key minutesKey = MinutesService.put("テスト用議事録");
        for(int i = 0; i < 5; i++){
            MemoService.put(minutesKey, "memo" + i);
        }
        assertThat("実行前のメモ数は5", MemoService.list(minutesKey).size(), is(5));

        Minutes minutes = Datastore.get(Minutes.class, minutesKey);
        BlobKey blobKey = MinutesService.exportAsTsv(minutes);

        assertThat("blobKeyが返される", blobKey, is(notNullValue()));
        File file = new File("www-test/WEB-INF/appengine-generated/" + blobKey.getKeyString());
        assertThat("ファイルが作成される", file.exists(), is(true));

        BufferedReader r = new BufferedReader(new FileReader(file));
        String line;
        int index = 0;
        while((line = r.readLine()) != null){
            String[] split = line.split("\t");
            assertThat(split.length, is(3));
            assertThat(index + "行目：投稿日時", split[0], is(notNullValue()));
            assertThat(index + "行目：投稿者", split[1], is("\"test@example.com\""));
            assertThat(index + "行目：内容", split[2], is("\"" + "memo" + index + "\""));
            index++;
        }
    }

    @Test
    public void メモ件数を加算した際にProspectiveSerchを実行する(){
        Key minutesKey = MinutesService.put("テスト用議事録１");
        MinutesService.incrementMemoCount(minutesKey);
        assertThat("ProspectiveSearchのmatch()が実行されている", delegate.matchWasExecuted, is(true));
    }
}
