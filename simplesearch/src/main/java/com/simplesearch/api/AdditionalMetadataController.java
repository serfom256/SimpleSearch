package com.simplesearch.api;

import com.simplesearch.common.statistics.SimpleSearchStatistics;
import com.simplesearch.model.internal.shard.ShardInfoHeader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AdditionalMetadataController {

    private final SimpleSearchStatistics statistics;

    @Autowired
    public AdditionalMetadataController(SimpleSearchStatistics statistics) {
        this.statistics = statistics;
    }

    @GetMapping("/shards")
    public ResponseEntity<ShardInfoHeader> createIndexes() {
        return ResponseEntity.ok(statistics.getShardsState());
    }
}
