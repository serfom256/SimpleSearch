package com.opensearch.core;

import com.opensearch.entity.LookupResult;

import java.util.List;

public class Shard {

    private final QueryService service;
    private final String name;

    public Shard(String name) {
        this.name = name;
        service = new QueryService(new TrieMap());
    }

    public void save(String key, int metadataId) {
        service.save(key, metadataId);
    }

    public List<LookupResult> find(String query, int distance, int count) {
        return service.find(query, distance, count);
    }

    public List<LookupResult> suggest(String query, int distance, int count) {
        return service.matchPrefix(query, distance, count);
    }

    public String getName() {
        return name;
    }

    public int getIndexedSize() {
        return service.getMapSize();
    }
}

