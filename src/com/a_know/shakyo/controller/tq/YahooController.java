package com.a_know.shakyo.controller.tq;

import java.util.Set;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.a_know.shakyo.meta.MemoMeta;
import com.a_know.shakyo.model.KeyWord;
import com.a_know.shakyo.model.Memo;
import com.a_know.shakyo.service.YahooAPIService;
import com.google.appengine.api.datastore.Key;

public class YahooController extends Controller {

    @Override
    public Navigation run() throws Exception {
        Key memoKey = asKey("memoKey");

        Memo memo = Datastore.get(MemoMeta.get(), memoKey);
        Set<String> keywords = YahooAPIService.parse(memo.getMemo());

        KeyWord keyWord = new KeyWord();
        keyWord.setKey(Datastore.createKey(memoKey, KeyWord.class, "1"));//【参考】親キーとしてMemoエンティティのキーを設定してKeyWordのキーを作る。getParent()で親キーの取得が可能に
        keyWord.setWords(keywords);
        Datastore.put(keyWord);

        return null;
    }
}
