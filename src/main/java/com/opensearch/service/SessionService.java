package com.opensearch.service;


import com.opensearch.entity.session.SessionInfo;
import com.opensearch.entity.session.SessionStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_SESSION = "SELECT * FROM session WHERE uuid = ?";
    private static final String SAVE_SESSION = "INSERT INTO session (uuid, status, create_time) VALUES (?, ?, ?)";

    public SessionService() {
        jdbcTemplate = new JdbcTemplate();
    }

    public SessionInfo getSessionInfo(String uuid) {
        return jdbcTemplate.queryForObject(GET_SESSION, (rs, rowNum) -> new SessionInfo(
                rs.getString("uuid"),
                SessionStatus.valueOf(rs.getString("status")),
                new java.util.Date(rs.getTime("create_time").getTime()),
                null,
                null
        ), uuid);
    }

    public void saveSessionInfo(SessionInfo sessionInfo) {
        jdbcTemplate.update(
                SAVE_SESSION,
                sessionInfo.getSessionUUID(),
                sessionInfo.getStatus(),
                sessionInfo.getCreateTime()
        );
    }
//    Todo crate context
//    Todo delete sessions by current user
}
