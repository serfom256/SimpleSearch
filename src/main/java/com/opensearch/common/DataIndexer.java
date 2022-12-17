package com.opensearch.common;

import com.opensearch.core.Shard;
import com.opensearch.entity.document.Document;
import com.opensearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
@Component
public class DataIndexer {

    private final SearchService searchService;
    private static final int THREAD_POOL_SIZE = 4;
    private static final int DATA_SIZE_THRESHOLD = 10_000;
    private final ExecutorService executorService;

    @Autowired
    public DataIndexer(SearchService searchService) {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.searchService = searchService;
    }

    private void makeIndexesAsync(Map<String, List<Document>> indexes, List<Shard> shards) {
        int i = 0;
        for (Map.Entry<String, List<Document>> e : indexes.entrySet()) {
            for (Document md : e.getValue()) {
                shards.get(++i % shards.size()).save(e.getKey(), searchService.serialize(e.getKey(), md));
            }
        }
    }

    public void makeIndexesFor(List<Shard> shards, Map<String, List<Document>> indexes, int threads) {
        executorService.submit(() -> {
            try {
                if (indexes.size() < DATA_SIZE_THRESHOLD) {
                    makeIndexesAsync(indexes, shards);
                } else {
                    makeIndexes(shards, indexes, threads);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void makeIndexes(List<Shard> shards, Map<String, List<Document>> indexes, int threads) {
        List<Thread> threadList = new ArrayList<>(threads);
        List<Map.Entry<String, List<Document>>> entries = new ArrayList<>(indexes.entrySet());
        int start = 0, gap = indexes.size() / threads;
        for (int i = 0; i < threads; i++, start += gap) {
            IndexingThread indexingThread = new IndexingThread(shards, entries, start, start + gap);
            indexingThread.start();
            threadList.add(indexingThread);
        }

        Thread last = new IndexingThread(shards, entries, start, start + (threadList.size() - start));
        threadList.add(last);
        last.start();
        try {
            waitForCompletion(threadList);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        shards.forEach(shard -> log.info("Shard: " + shard.getName() + " indexed values: " + shard.getIndexedSize()));
        printUsedMemory();
    }

    private void waitForCompletion(List<Thread> threads) throws InterruptedException {
        for (Thread t : threads) {
            t.join();
        }
    }

    private class IndexingThread extends Thread {

        private final List<Shard> shardList;
        private final List<Map.Entry<String, List<Document>>> indexes;
        private final int start;
        private final int end;

        IndexingThread(List<Shard> shardList, List<Map.Entry<String, List<Document>>> indexes, int start, int end) {
            this.shardList = shardList;
            this.indexes = indexes;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            if (start >= end) return;
            int i = 0;
            for (int j = start; j < end; j++) {
                Map.Entry<String, List<Document>> entry = indexes.get(j);
                for (Document md : entry.getValue()) {
                    shardList.get(++i % shardList.size()).save(entry.getKey(), searchService.serialize(entry.getKey(), md));
                }
            }
        }
    }

    private void printUsedMemory() {
        int mb = 1024 * 1024;
        Runtime instance = Runtime.getRuntime();
        log.info("Used Memory: " + (instance.totalMemory() - instance.freeMemory()) / mb);
    }
}
