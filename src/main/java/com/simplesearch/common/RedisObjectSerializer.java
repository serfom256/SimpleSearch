package com.simplesearch.common;

import com.simplesearch.entity.document.Document;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
public class RedisObjectSerializer implements RedisSerializer<Document> {

    @Override
    public byte[] serialize(Document t) throws SerializationException {
        return SerializationUtils.serialize(t);
    }

    @Override
    public Document deserialize(byte[] bytes) throws SerializationException {
        return (Document) SerializationUtils.deserialize(bytes);
    }
}

