package com.simplesearch.service;


import com.simplesearch.entity.document.Document;
import com.simplesearch.entity.session.IndexingSession;
import com.simplesearch.entity.session.SessionDTO;
import com.simplesearch.entity.session.SessionStatus;
import com.simplesearch.exceptions.SessionNotFoundException;
import com.simplesearch.indexation.DataIndexer;
import com.simplesearch.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository repository;
    private final DataIndexer dataIndexer;
    @Autowired
    public SessionService(SessionRepository repository, DataIndexer dataIndexer) {
        this.repository = repository;
        this.dataIndexer = dataIndexer;
    }

    public IndexingSession startIndexing(Map<String, List<Document>> entities, int indexedTotal) {
        IndexingSession newSession = createSession(indexedTotal);
        SessionDTO sessionDTO = new SessionDTO(new Date(), newSession);
        repository.saveSession(newSession);
        dataIndexer.makeIndexesFor(entities, sessionDTO);
        return newSession;
    }

    public IndexingSession getIndexingSessionById(String id) {
        IndexingSession session = repository.findSessionInfo(id);
        if (session == null) throw new SessionNotFoundException(id);
        int indexed = dataIndexer.getIndexedCountBySessionId(id);
        if (indexed == -1) return session;
        session.setIndexed(indexed);
        return session;
    }

    private IndexingSession createSession(int total) {
        return new IndexingSession(UUID.randomUUID().toString(), SessionStatus.IN_PROGRESS, new Date(), -1, total);
    }

}
