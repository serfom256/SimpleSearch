package com.simplesearch.api;

import com.simplesearch.model.session.IndexingSession;
import com.simplesearch.service.SessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/v1")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<IndexingSession> session(@PathVariable String id) {
        return ResponseEntity.ok(sessionService.getIndexingSessionById(id));
    }
}
