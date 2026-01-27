package com.simplesearch.api;

import com.simplesearch.model.session.IndexingSession;
import com.simplesearch.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RequiredArgsConstructor
public class SessionController implements SimpleSearchApi {

    private final SessionService sessionService;

    @GetMapping("/session/{id}")
    public ResponseEntity<IndexingSession> session(@PathVariable String id) {
        return ResponseEntity.ok(sessionService.getIndexingSessionById(id));
    }
}
