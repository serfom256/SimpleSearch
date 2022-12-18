package com.opensearch.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Log4j2
@Component
@ConditionalOnProperty(value = "opensearch.ddl-on-startup", havingValue = "true")
public class DbCreator {

    private final DataSource dataSource;

    @Autowired
    public DbCreator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    private void initTablesStructure() {
        Resource resource = new ClassPathResource("db/datasource_init.sql");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
        populator.execute(dataSource);
        log.info("Table structure restored");
    }
}
