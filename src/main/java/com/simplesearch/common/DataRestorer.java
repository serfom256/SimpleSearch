package com.simplesearch.common;

import com.simplesearch.core.entity.ShardList;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;


@Log4j2
@Component
@DependsOn("dbCreator")
@ConditionalOnProperty(value = "simplesearch.restore.data", havingValue = "true")
public class DataRestorer {

    private final JdbcTemplate template;
    private static final String SELECT_ALL_QUERY = "SELECT * from data";
    private final ShardList shards;

    @Autowired
    public DataRestorer(JdbcTemplate template, ShardList shards) {
        this.template = template;
        this.shards = shards;
    }

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

