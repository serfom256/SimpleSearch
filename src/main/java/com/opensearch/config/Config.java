package com.opensearch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Config {

    private final Environment environment;

    @Autowired
    public Config(Environment environment) {
        this.environment = environment;
    }

    public String getProperty(String key, String defaultProperty) {
        String property = environment.getProperty(key);

        if (property == null) {
            if (defaultProperty != null) return defaultProperty;
            throw new IllegalStateException(String.format("Property %s not found", key));
        }
        return property;
    }

}
