package com.opensearch.common;

import com.opensearch.config.Config;
import com.opensearch.core.balancer.LoadBalancer;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.sql.ResultSet;

import static com.opensearch.config.GlobalConstants.SHARDS_USED;


@Log4j2
@Component
public class DataRestorer {

    private final Environment env;
    private final LoadBalancer balancer;
    private final JdbcTemplate template;
    private final int shardsCount;
    private static final String SELECT_ALL_QUERY = "SELECT * from data";

    @Autowired
    public DataRestorer(LoadBalancer balancer, Environment env, JdbcTemplate template, Config config) {
        this.balancer = balancer;
        this.env = env;
        this.template = template;
        this.shardsCount = Integer.parseInt(config.getProperty(SHARDS_USED.getValue(), null));
    }

    @PostConstruct
    private void restoreData() {
        if (Boolean.FALSE.equals(Boolean.valueOf(env.getProperty("opensearch.restore.data")))) {
            return;
        }
        new Thread(() -> template.query(SELECT_ALL_QUERY, new ResultSetExtractor<>() {
            int position = 0;

            @SneakyThrows
            @Override
            public Object extractData(ResultSet rs) throws DataAccessException {
                log.info("Restoring previous data");
                while (rs.next()) {
                    balancer.createSingleIndex(rs.getString("idx"), rs.getInt("id"), ++position % shardsCount);
                }
                log.info("Data from database restored");
                Thread.currentThread().interrupt();
                return null;
            }
        })).start();
    }
}
