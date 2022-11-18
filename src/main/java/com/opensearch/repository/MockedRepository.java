package com.opensearch.repository;

import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("test")
@Component
public class MockedRepository implements MetadataRepository {

    @Override
    public Integer serialize(String idx, Document metadata) {
        return new Random().nextInt();
    }

    @Override
    public Document deserialize(int id) {
        return new Document("/path/to/file", new Random().nextInt(), DocumentType.SIMPLE, "{\"amount\":\"100\"\n}");
    }
}
