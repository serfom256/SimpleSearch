package com.opensearch.controller;

import com.opensearch.core.LoadBalancer;
import com.opensearch.entity.Query;
import com.opensearch.entity.SearchResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;


@Log4j2
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
        log.debug(query);
         return CompletableFuture.completedFuture(balancer.search(query));
    }


    @PostMapping("index")
    public void indexAll() {

    }
}
