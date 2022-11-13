package com.opensearch.repository;


import com.opensearch.entity.ObjectMetadata;

public interface MetadataRepository {
    Integer serialize(ObjectMetadata metadata);

    ObjectMetadata deserialize(int id);
}
