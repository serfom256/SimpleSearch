package com.opensearch.repository;


import com.opensearch.entity.document.Document;

public interface MetadataRepository {
    Integer serialize(Document metadata);

    Document deserialize(int id);
}
