package com.simplesearch.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnvironmentWrapper {

    private final Environment environment;

    public String getProperty(String key, String defaultProperty) {
        String property = environment.getProperty(key);

        if (property == null) {
            if (defaultProperty != null) return defaultProperty;
            throw new IllegalStateException(String.format("Property %s not found", key));
        }
        return property;
    }

}
