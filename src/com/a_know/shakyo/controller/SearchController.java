package com.a_know.shakyo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;

import com.a_know.shakyo.meta.KeyWordMeta;
import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.model.KeyWord;
import com.a_know.shakyo.model.Memo;
import com.a_know.shakyo.service.YahooAPIService;
import com.google.appengine.api.datastore.Key;

public class SearchController extends Controller {

    @Override
    public Navigation run() throws Exception {
        String query = asString("query");
        Set<String> keyWords = YahooAPIService.parse(query);

        KeyWordMeta keyWordMeta = KeyWordMeta.get();

        //検索対象となるのはKeyWordエンティティ
        ModelQuery<KeyWord> modelQuery = Datastore.query(keyWordMeta);
        //【参考】複数のfilter条件を付加させる方法
        for(String keyWord : keyWords){
            //全てのキーワードをEQフィルタとして追加し、全てのキーワードにマッチする、というクエリを組み立てる
            modelQuery.filter(keyWordMeta.words.equal(keyWord));
        }

        //クエリを実行し、KeyWordエンティティのキーのリストを取得する
        List<Key> keyList = modelQuery.asKeyList();

        //KeyWordエンティティのキーのリストからMemoエンティティのキーを取得する
        Set<Key> memoKeyList = new HashSet<Key>(keyList.size());
        for(Key key : keyList){
            memoKeyList.add(key.getParent());//【参考】親キーの取得
        }

        //MemoエンティティのキーのリストからMemoエンティティを取得する
        List<Memo> memos = Datastore.get(Memo.class, memoKeyList);

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(MemoMeta.get().modelsToJson(memos));
        response.flushBuffer();

        return null;
    }
}
