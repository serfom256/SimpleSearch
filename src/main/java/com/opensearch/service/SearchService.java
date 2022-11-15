package com.opensearch.service;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.ObjectMetadata;
import com.opensearch.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final MetadataRepository repository;

    @Autowired
    public SearchService(MetadataRepository repository) {
        this.repository = repository;
    }

    public int serialize(ObjectMetadata metadata) {
        return repository.serialize(metadata);
    }

    public List<LookupResult> lookupForResults(List<LookupResult> results) {
        for (LookupResult res : results) {
            res.getSerializedIds().forEach(id -> res.setMetadata(repository.deserialize(id)));
        }
        return results;
    }
}
