package com.opensearch.core;

import com.opensearch.entity.session.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class SessionContext {
    private final Map<String, SessionInfo> sessionInfoMap;

    public SessionContext() {
        this.sessionInfoMap = new ConcurrentHashMap<>();
    }

    public void addSession(String uuid, SessionInfo sessionInfo) {
        sessionInfoMap.put(uuid, sessionInfo);
    }

    public SessionInfo removeSession(String uuid){
       return sessionInfoMap.remove(uuid);
    }
}
