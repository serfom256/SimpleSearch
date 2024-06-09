package com.simplesearch.indexation.impl;

import com.simplesearch.core.model.ShardList;
import com.simplesearch.indexation.DataIndexer;
import com.simplesearch.indexation.model.IndexingThread;
import com.simplesearch.model.internal.document.Document;
import com.simplesearch.model.session.SessionDTO;
import com.simplesearch.model.session.SessionStatus;
import com.simplesearch.repository.SessionRepository;
import com.simplesearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Primary
@Component
public class DataIndexerDefaultImpl implements DataIndexer {
    private final ShardList shards;
    private final SearchService searchService;
    private final ExecutorService executorService;
    private final SessionRepository repository;

    private final Map<String, List<IndexingThread>> runningSessions;

    private static final int THREAD_POOL_SIZE = 4;
    private static final int DATA_SIZE_THRESHOLD = 10_000;
    private static final int THREADS_COUNT = 10;

    @Autowired
    public DataIndexerDefaultImpl(SearchService searchService, ShardList shards, SessionRepository repository) {
        this.shards = shards;
        this.repository = repository;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.searchService = searchService;
        this.runningSessions = new ConcurrentHashMap<>();
    }

    @Override
    public void makeIndexesFor(Map<String, List<Document>> indexes, SessionDTO session) {
        final String sessionId = session.getIndexingSession().getId();
        executorService.submit(() -> {
            try {
                List<IndexingThread> indexingThreads = indexes.size() < DATA_SIZE_THRESHOLD ? makeIndexesSync(indexes)
                        : makeIndexesAsync(indexes);
                runningSessions.put(sessionId, indexingThreads);
                executeTasksAsync(indexingThreads);
                log.info(String.format("Indexing session: %s successfully completed", sessionId));
                session.getIndexingSession().setStatus(SessionStatus.DONE);
            } catch (Exception e) {
                e.printStackTrace();
                session.getIndexingSession().setStatus(SessionStatus.FAILED);
                log.info(String.format("Indexing session: %s failed", sessionId));
            }

            session.getIndexingSession().setIndexed(getIndexedCountBySessionId(sessionId));
            runningSessions.remove(sessionId);
            session.getIndexingSession()
                    .setDuration(new Date(new Date().getTime() - session.getSessionStart().getTime()));
            repository.updateSession(session.getIndexingSession());
        });
    }

    @Override
    public int getIndexedCountBySessionId(String sessionId) {
        List<IndexingThread> list = runningSessions.get(sessionId);
        if (list == null)
            return -1;
        return list.stream().mapToInt(IndexingThread::getIndexed).sum();
    }

    private List<IndexingThread> makeIndexesSync(Map<String, List<Document>> indexes) {
        List<Map.Entry<String, List<Document>>> entries = new ArrayList<>(indexes.entrySet());
        return Collections.singletonList(new IndexingThread(entries, 0, entries.size(), shards, searchService));
    }

    private List<IndexingThread> makeIndexesAsync(Map<String, List<Document>> indexes) {
        List<IndexingThread> threadList = new ArrayList<>(THREADS_COUNT);
        List<Map.Entry<String, List<Document>>> entries = new ArrayList<>(indexes.entrySet());
        int start = 0, gap = indexes.size() / THREADS_COUNT;
        for (int i = 0; i < THREADS_COUNT; i++, start += gap) {
            IndexingThread indexingThread = new IndexingThread(entries, start, start + gap, shards, searchService);
            threadList.add(indexingThread);
        }
        threadList.add(new IndexingThread(entries, start, entries.size(), shards, searchService));
        return threadList;
    }

    private void executeTasksAsync(List<IndexingThread> threads) {
        threads.forEach(Thread::start);
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                t.interrupt();
            }
        }
    }
}
