package com.simplesearch.api;

import com.simplesearch.model.session.IndexingSession;
import com.simplesearch.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SessionController {
    
    private final SessionService sessionService;

    @GetMapping("/session/{id}")
    public ResponseEntity<IndexingSession> session(@PathVariable String id) {
        return ResponseEntity.ok(sessionService.getIndexingSessionById(id));
    }
}
