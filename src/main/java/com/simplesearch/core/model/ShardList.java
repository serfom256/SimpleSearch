package com.simplesearch.core.model;

import com.simplesearch.config.Config;
import com.simplesearch.model.internal.shard.Shard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.simplesearch.config.GlobalConstants.SHARDS_USED;

@Component
public class ShardList implements Iterable<Shard> {

    private static final int DEFAULT_SHARDS = 6;
    private final List<Shard> shards;

    @Autowired
    public ShardList(Config config) {
        final int shardsAmount = Integer.parseInt(config.getProperty(SHARDS_USED.getValue(), String.valueOf(DEFAULT_SHARDS)));
        shards = initShards(shardsAmount);
    }

    private List<Shard> initShards(int shardsAmount) {
        final List<Shard> list = new ArrayList<>();
        for (int i = 0; i < shardsAmount; i++) {
            list.add(new Shard("shard-" + i));
        }
        return Collections.unmodifiableList(list);
    }

    public Shard get(int id) {
        return shards.get(id);
    }

    public List<Shard> getShards() {
        return shards;
    }

    public int size() {
        return shards.size();
    }

    @Override
    public Iterator<Shard> iterator() {
        return shards.iterator();
    }

    @Override
    public void forEach(Consumer<? super Shard> action) {
        shards.forEach(action);
    }

    @Override
    public Spliterator<Shard> spliterator() {
        return shards.spliterator();
    }

    public Stream<Shard> stream() {
        return shards.stream();
    }
}
