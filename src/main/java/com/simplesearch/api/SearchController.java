package com.simplesearch.api;

import com.simplesearch.core.search.AsyncQueryExecutor;
import com.simplesearch.entity.Query;
import com.simplesearch.entity.SearchResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;


@Log4j2
@RestController
@RequestMapping("/api/v1")
public class SearchController {

    private final AsyncQueryExecutor executor;

    @Autowired
    public SearchController(AsyncQueryExecutor executor) {
        this.executor = executor;
    }

    @Async
    @PostMapping("/search")
    public CompletableFuture<SearchResponse> search(@RequestBody Query query) {
        log.debug(query);
        return CompletableFuture.completedFuture(executor.search(query));
    }

    @Async
    @PostMapping("/suggest")
    public CompletableFuture<SearchResponse> suggest(@RequestBody Query query) {
        log.debug(query);
        return CompletableFuture.completedFuture(executor.suggest(query));
    }
}
