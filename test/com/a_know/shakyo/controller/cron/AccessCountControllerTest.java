package com.a_know.shakyo.controller.cron;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.a_know.shakyo.model.AccessCounter;
import com.a_know.shakyo.service.MinutesService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.TaskQueuePb;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Delegate;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;

public class AccessCountControllerTest extends ControllerTestCase {

    PullTasksDelegate delegate;

    @Override
    public void setUp() throws Exception{
        super.setUp();
        delegate = new PullTasksDelegate();
        ApiProxy.setDelegate(delegate);
    }

    @Override
    public void tearDown() throws Exception{
        ApiProxy.setDelegate(delegate.parent);
        super.tearDown();
    }

    static class PullTasksDelegate implements Delegate<Environment>{

        final Delegate<Environment> parent = ApiProxy.getDelegate();

        public void flushLogs(Environment arg0) {
            parent.flushLogs(arg0);
        }

        public List<Thread> getRequestThreads(Environment arg0) {
            return parent.getRequestThreads(arg0);
        }

        public void log(Environment arg0, LogRecord arg1) {
            parent.log(arg0, arg1);
        }

        public Future<byte[]> makeAsyncCall(Environment e, String p,
                String m, byte[] r, ApiConfig a) {
            return parent.makeAsyncCall(e, p, m, r, a);
        }

        List<TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task> tasks = new ArrayList<TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task>();

        public byte[] makeSyncCall(Environment e, String p, String m,
                byte[] r) throws ApiProxyException {
            if(p.equals("taskqueue") && m.equals("QueryAndOwnTasks")){
                TaskQueuePb.TaskQueueQueryAndOwnTasksResponse responsePb = new TaskQueuePb.TaskQueueQueryAndOwnTasksResponse();

                for(TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task task: tasks){
                    responsePb.addTask(task);
                }
                return responsePb.toByteArray();
            }else if(p.equals("taskqueue") && m.equals("Delete")){
                TaskQueuePb.TaskQueueDeleteRequest requestPb = new TaskQueuePb.TaskQueueDeleteRequest();
                requestPb.mergeFrom(r);
                int count = requestPb.taskNameSize();

                for(int i = 0; i < count; i++){
                    String taskName = requestPb.getTaskName(i);
                    ListIterator<TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task> j = tasks.listIterator();

                    while(j.hasNext()){
                        TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task task = j.next();
                        if(taskName.equals(task.getTaskName())){
                            j.remove();
                            break;
                        }
                    }
                }
                return new TaskQueuePb.TaskQueueDeleteResponse().toByteArray();
            }
            return parent.makeSyncCall(e, p, m, r);
        }

    }

    static final String PATH = "/cron/accessCount";

    @Test
    public void アクセスカウンタを集計する() throws Exception {
        List<Key> minutesKeys = createTestTasks();

        int before = tester.count(AccessCounter.class);

        tester.start(PATH);
        assertThat("AccessCountControllerのインスタンスが使用される", tester.getController(), instanceOf(AccessCountController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));

        int after = tester.count(AccessCounter.class);
        assertThat("テスト用の議事録の数だけアクセスカウンタが記録される", after, is(before + minutesKeys.size()));

        List<AccessCounter> counters = Datastore.query(AccessCounter.class).asList();

        for(AccessCounter a : counters){
            assertThat("全ての議事録につき２回のアクセスがある", a.getCount(), is(2L));
        }
        assertThat("タスクが全て削除される", delegate.tasks.size(), is(0));
    }

    private List<Key> createTestTasks(){
        List<Key> minutesKeys = new ArrayList<Key>();

        for(int i = 0; i < 5; i++){
            Key minutesKey = MinutesService.put("議事録" + i);
            minutesKeys.add(minutesKey);

            TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task task1 = new TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task();
            task1.setTaskName("task-first" + i);
            task1.setBody("minutesKey=" + Datastore.keyToString(minutesKey));
            delegate.tasks.add(task1);//１回目のアクセス

            TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task task2 = new TaskQueuePb.TaskQueueQueryAndOwnTasksResponse.Task();
            task2.setTaskName("task-first" + i);
            task2.setBody("minutesKey=" + Datastore.keyToString(minutesKey));
            delegate.tasks.add(task2);//１回目のアクセス
        }
        return minutesKeys;
    }
}
