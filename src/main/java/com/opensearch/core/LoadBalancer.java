package com.opensearch.core;

import com.opensearch.entity.*;
import com.opensearch.entity.document.Document;
import com.opensearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class LoadBalancer {

    private final List<Shard> shardList;
    private final SearchService searchService;
    private final ExecutorService executorService;
    private static final int SHARDS = 3;

    public LoadBalancer(SearchService searchService) {
        this.searchService = searchService;
        shardList = new ArrayList<>(SHARDS);
        executorService = Executors.newFixedThreadPool(SHARDS);
        initShards();
    }

    public SearchResponse search(final Query query) {
        long qTime = System.currentTimeMillis();
        SearchResponse response = new SearchResponse();
        List<LookupResult> result = searchService.lookupForResults(searchAsync(query));
        if (query.isSort()) result.sort(Comparator.comparingInt(a -> distance(a.getKey(), query.getToSearch())));
        List<LookupResult> collect = result.stream().limit(query.getCount()).collect(Collectors.toList());
        ResponseHeader header = new ResponseHeader();
        header.setQtime(System.currentTimeMillis() - qTime);
        header.setShardsUsed(3);
        response.setResultList(collect);
        header.setSorted(query.isSort());
        response.setHeader(header);
        return response;
    }

    private List<LookupResult> searchAsync(Query query) {
        List<Future<List<LookupResult>>> lookupRes = new ArrayList<>();
        String[] splitQuery = query.getToSearch().split(" ");
        for (String s : splitQuery) {
            final int distance = query.isFuzziness() ? getFuzziness(s) : query.getDistance();
            for (Shard shard : shardList) {
                lookupRes.add(executorService.submit(() -> shard.find(s, distance, query.getCount())));
            }
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
        for (int i = 0; i < SHARDS; i++) {
            shardList.add(new Shard("shard-" + i));
        }
    }

    private int distance(String s1, String s2) {
        int len1 = s1.length(), len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int a = 0;
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) ++a;
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + a, Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[len1][len2];
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
