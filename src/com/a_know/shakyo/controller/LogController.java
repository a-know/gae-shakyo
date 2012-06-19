package com.a_know.shakyo.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;

import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

import dto.LogDTO;
import dto.LogDTOMeta;

public class LogController extends Controller {

    static int LIMIT = 20;

    @Override
    public Navigation run() throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        LogQuery query = new LogQuery().batchSize(LIMIT).minLogLevel(LogLevel.INFO).includeAppLogs(true);

        if(StringUtil.isEmpty(asString("offset")) == false){
            query.offset(asString("offset"));
        }

        Iterator<RequestLogs> i = LogServiceFactory.getLogService().fetch(query).iterator();
        List<LogDTO> logs = new ArrayList<LogDTO>(LIMIT);
        int count = 0;

        while(i.hasNext() && count++ < LIMIT){
            RequestLogs log = i.next();
            LogDTO dto = new LogDTO();
            dto.setCombined(log.getCombined());
            dto.setOffset(log.getOffset());
            List<AppLogLine> logLines = log.getAppLogLines();

            for(AppLogLine logLine : logLines){
                dto.getLogLevels().add(logLine.getLogLevel().name());
                dto.getLogLines().add(logLine.getLogMessage());
            }
            logs.add(dto);
        }
        response.getWriter().println(LogDTOMeta.get().modelsToJson(logs));
        response.flushBuffer();
        return null;
    }
}
