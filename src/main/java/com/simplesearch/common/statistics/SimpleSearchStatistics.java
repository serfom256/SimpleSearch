package com.simplesearch.common.statistics;

import com.simplesearch.entity.shard.Shard;
import com.simplesearch.core.entity.ShardList;
import com.simplesearch.entity.shard.ShardInfoHeader;
import com.simplesearch.entity.shard.ShardState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SimpleSearchStatistics {

    private final ShardList shardList;

    @Autowired
    public SimpleSearchStatistics(ShardList shardList) {
        this.shardList = shardList;
    }

    public ShardInfoHeader getShardsState() {
        ShardInfoHeader shardInfoHeader = new ShardInfoHeader();
        shardInfoHeader.setShardsInfo(shardList.stream().map(s -> new ShardState(s.getName(), s.getIndexedSize())).collect(Collectors.toList()));
        shardInfoHeader.setIndexedTotal(shardList.stream().mapToInt(Shard::getIndexedSize).sum());
        shardInfoHeader.setCpu(Runtime.getRuntime().availableProcessors() + "");
        shardInfoHeader.setMemory(((int) getMemoryUsed()) + "mb");
        shardInfoHeader.setThreads(Thread.activeCount());
        return shardInfoHeader;
    }

    private float getMemoryUsed() {
        float mb = 1024 * 1024;
        Runtime instance = Runtime.getRuntime();
        return (instance.totalMemory() - instance.freeMemory()) / mb;
    }
}
