package com.simplesearch.model.internal.shard;

import com.simplesearch.core.QueryService;
import com.simplesearch.core.TrieMap;
import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.request.Query;

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

    public List<LookupResult> find(Query query) {
        int distance = query.getDistance() == null ? query.getToSearch().length() : query.getDistance();
        return service.find(query.getToSearch(), distance, query.getCount(), query.isFuzziness());
    }

    public List<LookupResult> suggest(Query query) {
        return service.matchPrefix(query.getToSearch(), query.getDistance(), query.getCount());
    }

    public String getName() {
        return name;
    }

    public int getIndexedSize() {
        return service.getMapSize();
    }
}

