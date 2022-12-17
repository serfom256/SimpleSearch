package com.opensearch.entity;

import lombok.Data;

import java.util.Set;

@Data
public class IndexingRequest {
    private String path;
    private Set<String> filesType;
    private Set<Character> separators;
}
