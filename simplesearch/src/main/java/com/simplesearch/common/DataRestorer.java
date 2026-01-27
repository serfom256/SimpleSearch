package com.simplesearch.common;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.simplesearch.core.model.ShardList;

import jakarta.annotation.PostConstruct;

import java.sql.ResultSet;


@Slf4j
@Component
@ConditionalOnProperty(value = "simplesearch.restore.data", havingValue = "true")
@RequiredArgsConstructor
public class DataRestorer {

    private final JdbcTemplate template;
    private static final String SELECT_ALL_QUERY = "SELECT * from data";
    private final ShardList shards;

    @PostConstruct
    private void restoreData() {
        final int shardsCount = Math.max(shards.size(), 1);
        new Thread(() -> template.query(SELECT_ALL_QUERY, new ResultSetExtractor<>() {
            int position = 0;

            @SneakyThrows
            @Override
            public Object extractData(ResultSet rs) throws DataAccessException {
                log.info("Restoring previous data");
                while (rs.next()) {
                    shards.get(++position % shardsCount).save(rs.getString("idx"), rs.getInt("id"));
                }
                log.info("Data from database restored");
                Thread.currentThread().interrupt();
                return null;
            }
        })).start();
    }
}

