package com.opensearch.controller;

import com.opensearch.core.LoadBalancer;
import com.opensearch.entity.IndexingResponse;
import com.opensearch.entity.Query;
import com.opensearch.entity.SearchResponse;
import com.opensearch.service.FileReadingService;
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
    private final FileReadingService fileReadingService;

    @Autowired
    public SearchController(LoadBalancer balancer, FileReadingService fileReadingService) {
        this.balancer = balancer;
        this.fileReadingService = fileReadingService;
    }

    @Async
    @PostMapping("search")
    public CompletableFuture<SearchResponse> search(@RequestBody Query query) {
        log.debug(query);
        return CompletableFuture.completedFuture(balancer.search(query));
    }


    @PostMapping("index")
    public IndexingResponse createIndexes(String document) {
        return fileReadingService.read(document);
    }
}
