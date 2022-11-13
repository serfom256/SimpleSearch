package com.opensearch.controller;

import com.opensearch.core.LoadBalancer;
import com.opensearch.entity.Query;
import com.opensearch.entity.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/v1")
public class SearchController {

    private final LoadBalancer balancer;

    @Autowired
    public SearchController(LoadBalancer balancer) {
        this.balancer = balancer;
    }

    @Async
    @PostMapping("search")
    public CompletableFuture<SearchResponse> search(@RequestBody Query query) {
         return CompletableFuture.completedFuture(balancer.search(query));
    }


    @PostMapping("index")
    public void indexAll() {

    }
}
