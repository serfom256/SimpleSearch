package com.simplesearch.api;

import com.simplesearch.common.statistics.SimpleSearchStatistics;
import com.simplesearch.model.internal.shard.ShardInfoHeader;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdditionalMetadataController implements SimpleSearchApi {

    private final SimpleSearchStatistics statistics;

    @GetMapping("/shards")
    public ResponseEntity<ShardInfoHeader> createIndexes() {
        return ResponseEntity.ok(statistics.getShardsState());
    }
}
