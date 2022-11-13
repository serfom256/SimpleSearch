package com.opensearch.repository;

import com.opensearch.entity.ObjectMetadata;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class MockedRepository implements MetadataRepository{

    @Override
    public Integer serialize(ObjectMetadata metadata) {
        return null;
    }

    @Override
    public ObjectMetadata deserialize(int id) {
        return null;
    }
}
