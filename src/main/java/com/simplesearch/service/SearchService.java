package com.simplesearch.service;

import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.internal.document.Document;
import com.simplesearch.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<LookupResult> lookup(List<LookupResult> results) {
        for (LookupResult res : results) {
            res.setMetadata(new ArrayList<>());
            res.getSerializedIds().forEach(id -> res.getMetadata().add(repository.deserialize(id)));
        }
        return results;
    }
}
