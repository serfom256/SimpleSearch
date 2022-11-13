package com.opensearch.core;

import com.opensearch.entity.*;
import com.opensearch.repository.MetadataRepository;
import com.opensearch.repository.ObjectMetadataRepository;
import com.opensearch.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class LoadBalancer {

    private final List<SearchService> shards;
    private final MetadataRepository repository;
    private final ExecutorService executorService;

    public LoadBalancer(MetadataRepository repository) {
        this.repository = repository;
        shards = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(3);
        initShards();
    }


    public SearchResponse search(final Query query) {
        List<Future<List<LookupResult>>> lookupRes = new ArrayList<>();
        long qTime = System.currentTimeMillis();
        SearchResponse response = new SearchResponse();
        for (SearchService searchService : shards) {
            lookupRes.add(executorService.submit(() -> searchService.search(query)));
        }
        Set<LookupResult> result = new HashSet<>();
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
        List<LookupResult> collect;
        if (query.isSort()) {
            collect = result.stream().sorted(Comparator.comparingInt(a -> distance(a.getKey(), query.getToSearch()))).limit(query.getCount()).collect(Collectors.toList());
        } else {
            collect = result.stream().limit(query.getCount()).collect(Collectors.toList());
        }
        ResponseHeader header = new ResponseHeader();
        header.setQtime(System.currentTimeMillis() - qTime);
        header.setShardsUsed(3);
        response.setResultList(collect);
        header.setSorted(query.isSort());
        response.setHeader(header);
        return response;
    }


    private void initShards() {
        for (int i = 0; i < 3; i++) {
            shards.add(new SearchService(repository));
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


    @PostConstruct
    public void index() {
        List<Thread> threads = new ArrayList<>();
        for (SearchService searchService : shards) {
            Thread thread = new Thread(() -> {
                List<Thread> threadList = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    Thread value = new Thread(() -> {
                        for (int k = 0; k < 300_000; k++) {
                            searchService.add(generateString(4, 15), new ObjectMetadata("/path/to/file", (int) (Math.random() * 10000)));
                        }
                    });
                    value.start();
                    threadList.add(value);
                }
                for (Thread t : threadList) {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        for (int i = 0; i < shards.size(); i++) {
            log.info("Shard: " + i + " indexed values: " + shards.get(i).getIndexedSize());
        }
        int mb = 1024 * 1024;
        Runtime instance = Runtime.getRuntime();
        log.info("Used Memory: " + (instance.totalMemory() - instance.freeMemory()) / mb);
    }

    private String generateString(int minLen, int maxLen) {
        int leftLimit = 97;
        int rightLimit = 122;
        int len = (int) ((Math.random() * (maxLen - minLen)) + minLen);
        StringBuilder s = new StringBuilder();
        new Random().ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(len)
                .forEach(s::appendCodePoint);
        return s.toString();
    }

}
