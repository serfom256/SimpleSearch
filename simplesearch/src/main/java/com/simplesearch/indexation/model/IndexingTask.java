package com.simplesearch.indexation.model;

import com.simplesearch.core.model.ShardList;
import com.simplesearch.model.internal.document.Document;
import com.simplesearch.service.SearchService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinTask;

@RequiredArgsConstructor
public class IndexingTask extends RecursiveTask<Void> {

    private final List<Map.Entry<String, List<Document>>> data;
    private final int start;
    private final int end;
    private final String sessionId;
    private int idx = 0;
    private final SearchService searchService;
    private final ShardList shards;
    private final Map<String, List<IndexingTask>> runningSessions;

    private static final int DATA_SIZE_THRESHOLD = 50_000;

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

    private List<IndexingTask> forkTasks() {
        List<IndexingTask> subtasks = new ArrayList<>();
        int pivot = (end + start) / 2;
        subtasks.add(new IndexingTask(data, pivot + 1, start, sessionId, searchService, shards, runningSessions));
        subtasks.add(new IndexingTask(data, end, pivot - 1, sessionId, searchService, shards, runningSessions));
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
