package com.opensearch.core.balancer;

import com.opensearch.common.DataIndexer;
import com.opensearch.common.chain.DefaultChainBuilder;
import com.opensearch.config.Config;
import com.opensearch.core.SessionContext;
import com.opensearch.core.Shard;
import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;
import com.opensearch.entity.SearchResponse;
import com.opensearch.entity.document.Document;
import com.opensearch.entity.session.SessionInfo;
import com.opensearch.entity.session.SessionStatus;
import com.opensearch.entity.shard.ShardInfoHeader;
import com.opensearch.entity.shard.ShardState;
import com.opensearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.opensearch.config.GlobalConstants.SHARDS_USED;

@Log4j2
@Component
public class LoadBalancer {

    private final List<Shard> shardList;
    private final SearchService searchService;
    private final ExecutorService executorService;
    private final DataIndexer dataIndexer;
    private static final int DEFAULT_SHARDS = 6;
    private final int shards;
    private final DefaultChainBuilder builder;
    private final BiFunction<Shard, Query, List<LookupResult>> findFunc = Shard::find;
    private final BiFunction<Shard, Query, List<LookupResult>> suggestFunc = Shard::suggest;
    private final SessionContext sessionContext;

    @Autowired
    public LoadBalancer(SearchService searchService, DataIndexer dataIndexer, Config config, SessionContext sessionContext) {
        this.searchService = searchService;
        this.dataIndexer = dataIndexer;
        shards = Integer.parseInt(config.getProperty(SHARDS_USED.getValue(), String.valueOf(DEFAULT_SHARDS)));
        shardList = new ArrayList<>(shards);
        executorService = Executors.newFixedThreadPool(shards);
        initShards();
        builder = new DefaultChainBuilder();
        this.sessionContext = sessionContext;
    }

    public SearchResponse suggest(final Query query) {
        long qTime = System.currentTimeMillis();
        List<LookupResult> results = searchAsync(suggestFunc, query);
        List<LookupResult> withMetadata = searchService.lookupForResults(results);
        return builder.executeQueryChain(query, withMetadata, qTime, shards);
    }

    public SearchResponse search(final Query query) {
        long qTime = System.currentTimeMillis();
        List<LookupResult> results = searchAsync(findFunc, query);
        List<LookupResult> withMetadata = searchService.lookupForResults(results);
        return builder.executeQueryChain(query, withMetadata, qTime, shards);
    }

    private List<LookupResult> searchAsync(BiFunction<Shard, Query, List<LookupResult>> findFunc, Query query) {
        List<Future<List<LookupResult>>> lookupRes = new ArrayList<>();
        for (Shard shard : shardList) {
            lookupRes.add(executorService.submit(() -> findFunc.apply(shard, query)));
        }
        List<LookupResult> result = new ArrayList<>();
        Map<String, Integer> prev = new HashMap<>();
        for (var future : lookupRes) {
            try {
                List<LookupResult> lookupResults = future.get();
                for (LookupResult r : lookupResults) {
                    Integer key = prev.get(r.getKey());
                    if (key == null) {
                        prev.put(r.getKey(), result.size());
                        result.add(r);
                    } else {
                        result.get(key).getSerializedIds().addAll(r.getSerializedIds());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return result;
    }

    private void initShards() {
        for (int i = 0; i < shards; i++) {
            shardList.add(new Shard("shard-" + i));
        }
    }

    private SessionInfo createSession(int total){
        return new SessionInfo(UUID.randomUUID().toString(), SessionStatus.IN_PROGRESS,new Date(), 0, total);
    }

    public SessionInfo createIndexesForCollection(Map<String, List<Document>> indexes) {
        SessionInfo newSession = createSession(indexes.size());
        sessionContext.addSession(newSession.getSessionUUID(), newSession);
        dataIndexer.makeIndexesFor(shardList, indexes, 10, newSession);// todo set threads count
        return newSession;
    }

    public void createSingleIndex(String idx, int docId, int shardId) {
        shardList.get(shardId).save(idx, docId);
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
