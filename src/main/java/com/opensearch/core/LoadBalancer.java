package com.opensearch.core;

import com.opensearch.common.chain.DefaultChainBuilder;
import com.opensearch.config.Config;
import com.opensearch.entity.*;
import com.opensearch.entity.document.Document;
import com.opensearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.opensearch.config.GlobalConstants.SHARDS_USED;

@Log4j2
@Component
public class LoadBalancer {

    private final List<Shard> shardList;
    private final SearchService searchService;
    private final ExecutorService executorService;
    private static final int DEFAULT_SHARDS = 6;
    private final int shards;
    private final DefaultChainBuilder builder;

    @Autowired
    public LoadBalancer(SearchService searchService, Config config) {
        this.searchService = searchService;
        shards = Integer.parseInt(config.getProperty(SHARDS_USED.getValue(), String.valueOf(DEFAULT_SHARDS)));
        shardList = new ArrayList<>(shards);
        executorService = Executors.newFixedThreadPool(shards);
        initShards();
        builder = new DefaultChainBuilder();
    }

    public SearchResponse suggest(final Query query) {
        long qTime = System.currentTimeMillis();
        List<LookupResult> results = suggestAsync(query);
        return builder.executeQueryChain(query, results, qTime, shards);
    }

    public SearchResponse search(final Query query) {
        long qTime = System.currentTimeMillis();
        List<LookupResult> results = searchAsync(query);
        return builder.executeQueryChain(query, results, qTime, shards);
    }

    private List<LookupResult> searchAsync(Query query) {
        List<Future<List<LookupResult>>> lookupRes = new ArrayList<>();
        for (Shard shard : shardList) {
            lookupRes.add(executorService.submit(() -> shard.find(query.getToSearch(), query.getDistance(), query.getCount())));
        }
        List<LookupResult> result = new ArrayList<>();
        Set<String> prev = new HashSet<>();
        for (var future : lookupRes) {
            try {
                List<LookupResult> lookupResults = future.get();
                for (LookupResult r : lookupResults) {
                    if (!prev.contains(r.getKey())) result.add(r);
                    prev.add(r.getKey());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return result;
    }

    private List<LookupResult> suggestAsync(Query query) {
        List<Future<List<LookupResult>>> lookupRes = new ArrayList<>();
        for (Shard shard : shardList) {
            lookupRes.add(executorService.submit(() -> shard.findByPrefix(query.getToSearch(), query.getDistance(), query.getCount())));
        }
        List<LookupResult> result = new ArrayList<>();
        Set<String> prev = new HashSet<>();
        for (var future : lookupRes) {
            try {
                List<LookupResult> lookupResults = future.get();
                for (LookupResult r : lookupResults) {
                    if (!prev.contains(r.getKey())) result.add(r);
                    prev.add(r.getKey());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return result;
    }

    private int getFuzziness(String s) {
        int fuzziness = 0;
        for (int i = 1; i < s.length(); i += 3) {
            fuzziness++;
            i++;
        }
        return fuzziness;
    }

    private void initShards() {
        for (int i = 0; i < shards; i++) {
            shardList.add(new Shard("shard-" + i));
        }
    }

    public void createIndex(Map<String, List<Document>> indexes) {
        executorService.submit(() -> {
            try {
                makeIndex(indexes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void saveSingleIndex(String idx, int docId) {
        shardList.get(getShardIdx() % shardList.size()).save(idx, docId);
    }

    public List<ShardState> getShardsState() {
        return shardList.stream().map(s -> new ShardState(s.getName(), s.getIndexedSize())).collect(Collectors.toList());
    }

    private void makeIndex(Map<String, List<Document>> indexes) {
        int i = 0;
        for (Map.Entry<String, List<Document>> e : indexes.entrySet()) {
            for (Document md : e.getValue()) {
                shardList.get(++i % shardList.size()).save(e.getKey(), searchService.serialize(e.getKey(), md));
            }
        }
        shardList.forEach(shard -> log.info("Shard: " + shard.getName() + " indexed values: " + shard.getIndexedSize()));
        printUsedMemory();
    }

    private int getShardIdx() {
        return ThreadLocalRandom.current().nextInt(0, shardList.size() + 1);
    }

    private void printUsedMemory() {
        int mb = 1024 * 1024;
        Runtime instance = Runtime.getRuntime();
        log.info("Used Memory: " + (instance.totalMemory() - instance.freeMemory()) / mb);
    }


}
