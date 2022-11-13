package com.opensearch.service;

import com.opensearch.core.SearchTrieMap;
import com.opensearch.entity.LookupResult;
import com.opensearch.entity.ObjectMetadata;
import com.opensearch.entity.Query;
import com.opensearch.repository.MetadataRepository;

import java.util.List;

public class SearchService {

    private final SearchTrieMap trieMap;
    private final MetadataRepository repository;

    public SearchService(MetadataRepository repository) {
        this.repository = repository;
        trieMap = new SearchTrieMap();
    }

    public void add(String key, ObjectMetadata metadata) {
        Integer id = repository.serialize(metadata);
        trieMap.add(key, id);
    }

    public List<LookupResult> search(Query query) {
        List<LookupResult> lookup = trieMap.lookup(query.getToSearch(), query.getDistance(), query.getCount());
        for (LookupResult res : lookup) {
            res.getSerializedIds().forEach(id -> res.setMetadata(repository.deserialize(id)));
        }
        return lookup;
    }

    public int getIndexedSize() {
        return trieMap.getSize();
    }
}

