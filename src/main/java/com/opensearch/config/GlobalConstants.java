package com.opensearch.config;

public enum GlobalConstants {
    SHARDS_USED("opensearch.shards.used");

    private final String value;

    GlobalConstants(String data) {
        this.value = data;
    }

    public String getValue() {
        return value;
    }
}
