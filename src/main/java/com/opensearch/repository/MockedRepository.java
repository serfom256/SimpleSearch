package com.opensearch.repository;

import com.opensearch.entity.document.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("test")
@Component
public class MockedRepository implements MetadataRepository{

    @Override
    public Integer serialize(Document metadata) {
        return new Random().nextInt();
    }

    @Override
    public Document deserialize(int id) {
        return null;
    }
}
