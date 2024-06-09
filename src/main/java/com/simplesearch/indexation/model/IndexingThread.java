package com.simplesearch.indexation.model;

import java.util.List;
import java.util.Map;

import com.simplesearch.core.model.ShardList;
import com.simplesearch.model.internal.document.Document;
import com.simplesearch.service.SearchService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndexingThread extends Thread {

    private final List<Map.Entry<String, List<Document>>> indexes;
    private final int start;
    private int idx;
    private final int end;
    private final ShardList shards;
    private final SearchService searchService;

    @Override
    public void run() {
        if (start >= end){
            return;
        }
        int i = 0;
        final int shardsCount = Math.max(shards.size(), 1);
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