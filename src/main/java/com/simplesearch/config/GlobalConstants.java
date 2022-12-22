package com.simplesearch.config;

public enum GlobalConstants {
    SHARDS_USED("simplesearch.shards.used"),
    DDL_ON_STARTUP("simplesearch.ddl-on-startup");

    private final String value;

    GlobalConstants(String data) {
        this.value = data;
    }

    public String getValue() {
        return value;
    }
}
