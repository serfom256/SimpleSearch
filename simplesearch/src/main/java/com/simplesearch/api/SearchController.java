package com.simplesearch.api;

import com.simplesearch.core.search.AsyncQueryExecutor;
import com.simplesearch.model.request.Query;
import com.simplesearch.model.response.SearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class SearchController implements SimpleSearchApi {

    private final AsyncQueryExecutor executor;

    @PostMapping("/search")
    public CompletableFuture<SearchResponse> search(@RequestBody Query query) {
        log.debug("{}", query);
        return CompletableFuture.completedFuture(executor.search(query));
    }

    @PostMapping("/suggest")
    public CompletableFuture<SearchResponse> suggest(@RequestBody Query query) {
        log.debug("{}", query);
        return CompletableFuture.completedFuture(executor.suggest(query));
    }
}
