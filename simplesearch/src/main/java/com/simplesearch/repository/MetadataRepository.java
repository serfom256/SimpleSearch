package com.simplesearch.repository;


import com.simplesearch.model.internal.document.Document;

public interface MetadataRepository {
    Integer serialize(String idx, Document metadata);

    Document deserialize(int id);
}
