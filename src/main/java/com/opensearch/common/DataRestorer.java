package com.opensearch.common;

import com.opensearch.core.LoadBalancer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
@Component
public class DataRestorer {

    private final LoadBalancer balancer;
    private final Environment env;
    private final JdbcTemplate template;
    private final ExecutorService executorService;
    private static final String SELECT_ALL_QUERY = "SELECT * from data";

    @Autowired
    public DataRestorer(LoadBalancer balancer, Environment env, JdbcTemplate template) {
        this.balancer = balancer;
        this.env = env;
        this.template = template;
        executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    private void restoreData() {
        if (Boolean.FALSE.equals(Boolean.valueOf(env.getProperty("opensearch.restore.data")))) {
            return;
        }
        log.info("Restoring previous data");
        executorService.execute(() -> template.query(SELECT_ALL_QUERY, rs -> {
            balancer.saveSingleIndex(rs.getString("idx"), rs.getInt("id"));
            if (rs.isFirst()) log.info("Data from database restored");
        }));

    }
}
