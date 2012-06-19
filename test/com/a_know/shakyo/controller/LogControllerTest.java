package com.a_know.shakyo.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slim3.tester.ControllerTestCase;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;
import com.google.apphosting.api.logservice.LogServicePb;
import com.google.apphosting.api.logservice.LogServicePb.LogLine;
import com.google.apphosting.api.logservice.LogServicePb.LogOffset;
import com.google.apphosting.api.logservice.LogServicePb.RequestLog;

import dto.LogDTO;
import dto.LogDTOMeta;

public class LogControllerTest extends ControllerTestCase {

    static final String PATH = "/log";

    @Test
    public void ログを取得する() throws Exception {
        LogDelegate delegate = new LogDelegate();
        ApiProxy.setDelegate(delegate);

        tester.start(PATH);

        assertThat("LogControllerのインスタンスが使用される", tester.getController(), instanceOf(LogController.class));
        assertThat("レスポンスコードが200", tester.response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat("Content-typeはappication/json", tester.response.getContentType(), is("application/json"));

        LogDTO[] logs = LogDTOMeta.get().jsonToModels(tester.response.getOutputAsString());
        assertThat(logs.length, is(20));
        ApiProxy.setDelegate(delegate.parentDelegate);
    }

    static class LogDelegate implements ApiProxy.Delegate<ApiProxy.Environment>{

        final ApiProxy.Delegate<ApiProxy.Environment> parentDelegate = ApiProxy.getDelegate();

        public void flushLogs(Environment arg0) {
            parentDelegate.flushLogs(arg0);

        }

        public List<Thread> getRequestThreads(Environment arg0) {
            return parentDelegate.getRequestThreads(arg0);
        }

        public void log(Environment arg0, LogRecord arg1) {
            parentDelegate.log(arg0, arg1);
        }

        public Future<byte[]> makeAsyncCall(Environment e, String s,
                String m, byte[] r, ApiConfig a) {
            if(s.equals("logservice") == false || m.equals("Read") == false){
                return parentDelegate.makeAsyncCall(e, s, m, r, a);
            }
            LogServicePb.LogReadRequest requestPb = new LogServicePb.LogReadRequest();
            requestPb.mergeFrom(r);
            final LogServicePb.LogReadResponse responsePb = new LogServicePb.LogReadResponse();

            for(int i = 0;i < requestPb.getCount(); i++){
                RequestLog log = new RequestLog();
                log.setCombined("0.0.0.0 - LoginUser [01/Dec/2011:00:00:00 -0000]" + " \"GET /path HTTP/1.1\" 200 100 \"http://google.com/\" \"Chrome\"");
                log.setOffset(new LogOffset().setRequestId(String.valueOf(i)));
                log.addLine(new LogLine().setLogMessage("\npath\nException:\"\nmessage\"\n\\"));
                responsePb.addLog();
            }
            return new Future<byte[]>() {

                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                public boolean isCancelled() {
                    return false;
                }

                public boolean isDone() {
                    return true;
                }

                public byte[] get() throws InterruptedException,
                        ExecutionException {
                    return responsePb.toByteArray();
                }

                public byte[] get(long timeout, TimeUnit unit)
                        throws InterruptedException, ExecutionException,
                        TimeoutException {
                    return get();
                }
            };
        }

        public byte[] makeSyncCall(Environment arg0, String arg1, String arg2,
                byte[] arg3) throws ApiProxyException {
            return parentDelegate.makeSyncCall(arg0, arg1, arg2, arg3);
        }

    }
}
