package com.simplesearch.core.search;

import com.simplesearch.common.chain.DefaultChainBuilder;
import com.simplesearch.core.model.ShardList;
import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.internal.shard.Shard;
import com.simplesearch.model.request.Query;
import com.simplesearch.model.response.SearchResponse;
import com.simplesearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

@Component
public class AsyncQueryExecutor {

    private final ShardList shardList;
    private final ExecutorService executorService;
    private final SearchService searchService;
    private final DefaultChainBuilder builder;

    private final BiFunction<Shard, Query, List<LookupResult>> findFunc = Shard::find;
    private final BiFunction<Shard, Query, List<LookupResult>> suggestFunc = Shard::suggest;


    @Autowired
    public AsyncQueryExecutor(ShardList shardList, SearchService searchService) {
        this.shardList = shardList;
        this.executorService = Executors.newFixedThreadPool(24);
        this.searchService = searchService;
        this.builder = new DefaultChainBuilder();
    }

    public SearchResponse suggest(final Query query) {
        long qTime = System.currentTimeMillis();
        List<LookupResult> results = searchAsync(suggestFunc, query);
        List<LookupResult> withMetadata = searchService.lookup(results);
        return builder.executeQueryChain(query, withMetadata, qTime, shardList.size());
    }

    public SearchResponse search(final Query query) {
        long qTime = System.currentTimeMillis();
        List<LookupResult> results = searchAsync(findFunc, query);
        List<LookupResult> withMetadata = searchService.lookup(results);
        return builder.executeQueryChain(query, withMetadata, qTime, shardList.size());
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

}
