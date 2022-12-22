package com.simplesearch.config;

import com.simplesearch.common.LogoPrinter;
import com.simplesearch.common.PropertiesPrinter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;


@Log4j2
@Configuration
@DependsOn("dbCreator")
public class EnvironmentPropertiesInitializer implements BeanPostProcessor, InitializingBean, EnvironmentAware {

    private final JdbcTemplate jdbcTemplate;
    private ConfigurableEnvironment environment;
    private final PropertiesPrinter printer;
    private final LogoPrinter logoPrinter;

    private static final String CONFIG_TABLE = "search_config";
    private static final String SELECT_ALL_CONFIG = "SELECT * from " + CONFIG_TABLE;

    public EnvironmentPropertiesInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        printer = new PropertiesPrinter();
        logoPrinter = new LogoPrinter();
    }

    @Override
    public void afterPropertiesSet() {
        logoPrinter.printLogo();
        if (environment != null) {
            final Map<String, Object> properties = new HashMap<>();
            jdbcTemplate.query(SELECT_ALL_CONFIG, rs -> {
                String key = rs.getString("name");
                String value = rs.getString("property");
                properties.put(key, value);
            });
            environment.getPropertySources().addFirst(new MapPropertySource(CONFIG_TABLE, properties));
            printer.printProperties(properties);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            this.environment = (ConfigurableEnvironment) environment;
        }
    }
}
