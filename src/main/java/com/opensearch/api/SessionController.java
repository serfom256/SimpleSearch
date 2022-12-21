package com.opensearch.api;

import com.opensearch.entity.session.SessionInfo;
import com.opensearch.entity.session.SessionRequest;
import com.opensearch.service.SessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("session")
    public ResponseEntity<SessionInfo> session(@RequestBody SessionRequest uuid){
        return ResponseEntity.ok(sessionService.getSessionInfo(uuid.getUuid()));
    }
}
