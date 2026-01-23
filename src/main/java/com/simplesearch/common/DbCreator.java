package com.simplesearch.common;

import com.simplesearch.config.GlobalConstants;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@RequiredArgsConstructor
class DbCreator {

    private final DataSource dataSource;
    private final Environment environment;

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
