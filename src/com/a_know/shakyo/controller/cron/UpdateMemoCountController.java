package com.a_know.shakyo.controller.cron;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.datastore.Key;

//GAEの処理時間の上限は１０分であることに注意。
public class UpdateMemoCountController extends Controller {

    @Override
    public Navigation run() throws Exception {
        List<Key> list = MinutesService.queryForUpdateMemoCount();
        for(Key minutesKey : list){
            MinutesService.updateMemoCount(minutesKey);
        }
        return null;
    }
}
