package com.simplesearch.repository;

import com.simplesearch.entity.session.IndexingSession;
import com.simplesearch.entity.session.SessionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional(rollbackFor = Exception.class)
public class SessionRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final String GET_SESSION_QUERY = "SELECT * FROM session WHERE uuid = ?";
    private static final String SAVE_SESSION_QUERY = "INSERT INTO session (uuid, status, total, indexed, duration) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SESSION_QUERY = "UPDATE session SET status = ?, indexed = ?, duration = ? WHERE uuid = ?";

    @Autowired
    public SessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public IndexingSession findSessionInfo(String uuid) {
        return jdbcTemplate.queryForObject(
                GET_SESSION_QUERY,
                (rs, rowNum) -> new IndexingSession(
                        rs.getString("uuid"),
                        SessionStatus.valueOf(rs.getString("status")),
                        new Date(rs.getTimestamp("duration").getTime()),
                        rs.getInt("indexed"),
                        rs.getInt("total")),
                uuid);
    }

    public void saveSession(IndexingSession indexingSession) {
        jdbcTemplate.update(
                SAVE_SESSION_QUERY,
                indexingSession.getId(),
                indexingSession.getStatus().name(),
                indexingSession.getTotal(),
                indexingSession.getIndexed(),
                indexingSession.getDuration());
    }

    public void updateSession(IndexingSession indexingSession) {
        jdbcTemplate.update(
                UPDATE_SESSION_QUERY,
                indexingSession.getStatus().name(),
                indexingSession.getIndexed(),
                indexingSession.getDuration(),
                indexingSession.getId());
    }

}
