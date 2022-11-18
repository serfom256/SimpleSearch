package com.opensearch.service;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.document.Document;
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

    public int serialize(String idx, Document metadata) {
        return repository.serialize(idx, metadata);
    }

    public List<LookupResult> lookupForResults(List<LookupResult> results) {
        for (LookupResult res : results) {
            res.getSerializedIds().forEach(id -> res.setMetadata(repository.deserialize(id)));
        }
        return results;
    }
}
