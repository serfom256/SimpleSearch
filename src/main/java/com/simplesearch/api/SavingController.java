package com.simplesearch.api;

import com.simplesearch.entity.IndexingRequest;
import com.simplesearch.entity.IndexingResponse;
import com.simplesearch.service.EntityIndexingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/v1")
public class SavingController {

    private final EntityIndexingService service;

    @Autowired
    public SavingController(EntityIndexingService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<IndexingResponse> createIndexes(@RequestBody IndexingRequest document) {
        log.debug(document);
        return ResponseEntity.ok(service.save(document));
    }
}
