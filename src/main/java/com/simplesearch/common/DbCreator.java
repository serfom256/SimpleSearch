package com.simplesearch.common;

import com.simplesearch.config.GlobalConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Log4j2
@Component
class DbCreator {

    private final DataSource dataSource;
    private final Environment environment;

    @Autowired
    public DbCreator(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @PostConstruct
    private void initTablesStructure() {
        String prop = environment.getProperty(GlobalConstants.DDL_ON_STARTUP.getValue());
        if (prop == null || prop.equals("false")) return;
        Resource resource = new ClassPathResource("db/db_init.sql");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
        populator.execute(dataSource);
        log.info("Tables structure restored");
    }
}
