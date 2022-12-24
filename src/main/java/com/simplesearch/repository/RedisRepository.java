package com.simplesearch.repository;

import com.simplesearch.common.RedisObjectSerializer;
import com.simplesearch.entity.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

//@Repository
public class RedisRepository implements MetadataRepository {

    private final RedisObjectSerializer serializer;
    private final RedisTemplate<Integer, Document> redisTemplate;

    @Autowired(required = false)
    public RedisRepository(RedisObjectSerializer serializer, RedisTemplate<Integer, Document> redisTemplate) {
        this.serializer = serializer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Integer serialize(String idx, Document metadata) {
        return null;
    }

    @Override
    public Document deserialize(int id) {
        return null;
    }
}

