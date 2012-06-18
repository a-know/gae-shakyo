package com.a_know.shakyo.controller.cron;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.a_know.shakyo.model.AccessCounter;
import com.a_know.shakyo.model.Minutes;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;

public class AccessCountController extends Controller {

    @Override
    public Navigation run() throws Exception {
        List<TaskHandle> tasks = QueueFactory.getQueue("access-log").leaseTasks(10, TimeUnit.MINUTES, 1000);
        Map<Key, Integer> countMap = createCounterMap(tasks);
        Datastore.put(createCounterEntities(countMap));
        QueueFactory.getQueue("access-log").deleteTask(tasks);
        return null;
    }

    /**
     * @param tasks leaseしたtask
     * @return AccessCounterエンティティのKey、集計したアクセスカウントのMap
     * @throws UnsupportedEncodingException
     */
    private Map<Key, Integer> createCounterMap(List<TaskHandle> tasks) throws UnsupportedEncodingException{
        Map<Key, Integer> countMap = new HashMap<Key, Integer>();
        for(TaskHandle task : tasks){
            String minutesKeyString = new String(task.getPayload(), "utf-8").split("=")[1];
            Key minutesKey = Datastore.stringToKey(minutesKeyString);
            Key counterKey = Datastore.createKey(AccessCounter.class, minutesKey.getId());//AccessCunterのキーとして、Miutesエンティティのid値を利用（★）

            if(countMap.containsKey(counterKey)){
                countMap.put(counterKey, countMap.get(counterKey) + 1);
            }else{
                countMap.put(counterKey, 1);
            }
        }
        return countMap;
    }

    /**
     * @param countMap
     * @return 集計結果を反映した、データベースに保存すべきAccessCounterエンティティのマップ
     */
    private Collection<AccessCounter> createCounterEntities(Map<Key, Integer> countMap){
        Map<Key, AccessCounter> counterEntitiesMap = Datastore.getAsMap(AccessCounter.class, countMap.keySet());
        Iterator<Entry<Key, Integer>> i = countMap.entrySet().iterator();

        while(i.hasNext()){
            Entry<Key, Integer> next = i.next();
            Key counterKey = next.getKey();
            AccessCounter counter;

            //既にエンティティが存在しているものとしていないものが混在しているので、ここで整理
            if(counterEntitiesMap.containsKey(counterKey)){
                counter = counterEntitiesMap.get(counterKey);
            }else{
                counter = new AccessCounter();
                counterEntitiesMap.put(counterKey, counter);
                counter.setKey(counterKey);
                Minutes minutes = Datastore.get(Minutes.class, Datastore.createKey(Minutes.class, counterKey.getId()));//（★）
                counter.setMinutesTitle(minutes.getTitle());
            }
            counter.setCount(counter.getCount() + next.getValue());
        }
        return counterEntitiesMap.values();
    }
}
