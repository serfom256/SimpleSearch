package com.opensearch.core;

import com.opensearch.entity.LookupResult;

import java.util.List;

public class Shard {

    private final SearchTrieMap trieMap;
    private final String name;

    public Shard(String name) {
        this.name = name;
        trieMap = new SearchTrieMap();
    }

    public void save(String key, int metadataId) {
        trieMap.add(key, metadataId);
    }

    public List<LookupResult> find(String query, int distance, int count) {
        return trieMap.lookup(query, distance, count);
    }

    public String getName() {
        return name;
    }

    public int getIndexedSize() {
        return trieMap.getSize();
    }
}

