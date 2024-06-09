package com.simplesearch.indexation.impl;

import com.simplesearch.core.model.ShardList;
import com.simplesearch.indexation.DataIndexer;
import com.simplesearch.indexation.model.IndexingTask;
import com.simplesearch.model.internal.document.Document;
import com.simplesearch.model.session.SessionDTO;
import com.simplesearch.model.session.SessionStatus;
import com.simplesearch.repository.SessionRepository;
import com.simplesearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Log4j2
//@Primary
@Component
public class RecursiveDataIndexer implements DataIndexer {

    private static final int THREAD_POOL_SIZE = 8;
    
    private final ForkJoinPool forkJoinPool;
    private final SearchService searchService;
    private final ShardList shards;
    private final SessionRepository repository;
    private final Map<String, List<IndexingTask>> runningSessions;

    public RecursiveDataIndexer(SearchService searchService, SessionRepository repository, ShardList shards) {
        this.searchService = searchService;
        this.repository = repository;
        this.shards = shards;
        this.forkJoinPool = new ForkJoinPool(THREAD_POOL_SIZE);
        this.runningSessions = new ConcurrentHashMap<>();
    }

    @Override
    public void makeIndexesFor(Map<String, List<Document>> indexes, SessionDTO session) {
        final String sessionId = session.getIndexingSession().getId();

        List<Map.Entry<String, List<Document>>> entries = new ArrayList<>(indexes.entrySet());
        runningSessions.put(sessionId, new ArrayList<>());
        final IndexingTask currentTask = new IndexingTask(entries, 0, entries.size(), sessionId, searchService, shards, runningSessions);
        try {
            forkJoinPool.execute(currentTask);
            currentTask.join();
            log.info(String.format("Indexing session: %s successfully completed", sessionId));
            session.getIndexingSession().setStatus(SessionStatus.DONE);
        } catch (Exception e) {
            e.printStackTrace();
            session.getIndexingSession().setStatus(SessionStatus.FAILED);
            log.info(String.format("Indexing session: %s failed", sessionId));
        }

        session.getIndexingSession().setIndexed(getIndexedCountBySessionId(sessionId));
        runningSessions.remove(sessionId);
        session.getIndexingSession().setDuration(new Date(new Date().getTime() - session.getSessionStart().getTime()));
        repository.updateSession(session.getIndexingSession());

    }

    @Override
    public int getIndexedCountBySessionId(String sessionId) {
        return 0;
    }
}
