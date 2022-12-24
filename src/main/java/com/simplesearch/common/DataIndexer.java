package com.simplesearch.common;

import com.simplesearch.core.entity.ShardList;
import com.simplesearch.entity.document.Document;
import com.simplesearch.entity.session.IndexingSession;
import com.simplesearch.entity.session.SessionStatus;
import com.simplesearch.repository.SessionRepository;
import com.simplesearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
@Component
public class DataIndexer {

    private final ShardList shards;
    private final SearchService searchService;
    private final ExecutorService executorService;
    private final SessionRepository repository;

    private final Map<String, List<IndexingThread>> runningSessions;

    private static final int THREAD_POOL_SIZE = 4;
    private static final int DATA_SIZE_THRESHOLD = 10_000;
    private static final int THREADS_COUNT = 10;

    @Autowired
    public DataIndexer(SearchService searchService, ShardList shards, SessionRepository repository) {
        this.shards = shards;
        this.repository = repository;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.searchService = searchService;
        this.runningSessions = new ConcurrentHashMap<>();
    }

    public void makeIndexesFor(Map<String, List<Document>> indexes, IndexingSession session) {
        final String sessionId = session.getId();
        executorService.submit(() -> {
            try {
                List<IndexingThread> indexingThreads = indexes.size() < DATA_SIZE_THRESHOLD ? makeIndexesSync(indexes) : makeIndexesAsync(indexes);
                runningSessions.put(sessionId, indexingThreads);
                executeTasksAsync(indexingThreads);
                log.info(String.format("Indexing session: %s successfully completed", sessionId));
                session.setStatus(SessionStatus.DONE);
            } catch (Exception e) {
                e.printStackTrace();
                session.setStatus(SessionStatus.FAILED);
                log.info(String.format("Indexing session: %s failed", sessionId));
            }
            session.setIndexed(getIndexedCountBySessionId(sessionId));
            runningSessions.remove(sessionId);
            repository.updateSession(session);
        });
    }

    public int getIndexedCountBySessionId(String sessionId) {
        List<IndexingThread> list = runningSessions.get(sessionId);
        if (list == null) return -1;
        return list.stream().mapToInt(IndexingThread::getIndexed).sum();
    }

    private List<IndexingThread> makeIndexesSync(Map<String, List<Document>> indexes) {
        List<Map.Entry<String, List<Document>>> entries = new ArrayList<>(indexes.entrySet());
        return Collections.singletonList(new IndexingThread(entries, 0, entries.size()));
    }

    private List<IndexingThread> makeIndexesAsync(Map<String, List<Document>> indexes) {
        List<IndexingThread> threadList = new ArrayList<>(THREADS_COUNT);
        List<Map.Entry<String, List<Document>>> entries = new ArrayList<>(indexes.entrySet());
        int start = 0, gap = indexes.size() / THREADS_COUNT;
        for (int i = 0; i < THREADS_COUNT; i++, start += gap) {
            IndexingThread indexingThread = new IndexingThread(entries, start, start + gap);
            threadList.add(indexingThread);
        }
        threadList.add(new IndexingThread(entries, start, entries.size()));
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

    private class IndexingThread extends Thread {

        private final List<Map.Entry<String, List<Document>>> indexes;
        private final int start;
        private int idx;
        private final int end;

        IndexingThread(List<Map.Entry<String, List<Document>>> indexes, int start, int end) {
            this.indexes = indexes;
            this.start = start;
            this.end = end;
            this.idx = 0;
        }

        @Override
        public void run() {
            if (start >= end) return;
            int i = 0;
            final int shardsCount = Math.max(shards.size() - 1, 1);
            for (int j = start; j < end; j++) {
                Map.Entry<String, List<Document>> entry = indexes.get(j);
                for (Document md : entry.getValue()) {
                    shards.get(++i % shardsCount).save(entry.getKey(), searchService.serialize(entry.getKey(), md));
                }
                idx = i;
            }
        }

        public int getIndexed() {
            return idx;
        }
    }
}
