package com.opensearch.entity;

import lombok.Data;

import java.util.List;

@Data
public class IndexingRequest {
    private String path;
    private List<Character> separators;
}
