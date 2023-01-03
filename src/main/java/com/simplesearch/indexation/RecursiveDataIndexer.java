package com.simplesearch.indexation;

import com.simplesearch.core.entity.ShardList;
import com.simplesearch.entity.document.Document;
import com.simplesearch.entity.session.SessionDTO;
import com.simplesearch.entity.session.SessionStatus;
import com.simplesearch.repository.SessionRepository;
import com.simplesearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
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
    private static final int DATA_SIZE_THRESHOLD = 50_000;
    private final ForkJoinPool forkJoinPool;
    private final SearchService searchService;
    private final ShardList shards;
    private final SessionRepository repository;
    private final Map<String, List<IndexationTask>> runningSessions;

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
        IndexationTask currTask = new IndexationTask(entries, 0, entries.size(), sessionId);
        try {
            forkJoinPool.execute(currTask);
            currTask.join();
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


    private class IndexationTask extends RecursiveTask<Void> {

        private final List<Map.Entry<String, List<Document>>> data;
        private final int start, end;
        private final String sessionId;
        private int idx;

        private IndexationTask(List<Map.Entry<String, List<Document>>> data, int start, int end, String sessionId) {
            this.data = data;
            this.end = end;
            this.start = start;
            this.sessionId = sessionId;
            this.idx = 0;
        }

        @Override
        protected Void compute() {
            if (start - end > DATA_SIZE_THRESHOLD) {
                ForkJoinTask.invokeAll(forkTasks());
            } else {
                runningSessions.get(sessionId).add(this);
                makeIndexes();
            }
            return null;
        }

        private List<IndexationTask> forkTasks() {
            List<IndexationTask> subtasks = new ArrayList<>();
            int pivot = (end + start) / 2;
            subtasks.add(new IndexationTask(data, pivot + 1, start, sessionId));
            subtasks.add(new IndexationTask(data, end, pivot - 1, sessionId));
            return subtasks;
        }

        private void makeIndexes() {
            int i = 0;
            final int shardsCount = Math.max(shards.size(), 1);
            for (int j = start; j < end; j++) {
                Map.Entry<String, List<Document>> entry = data.get(j);
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
