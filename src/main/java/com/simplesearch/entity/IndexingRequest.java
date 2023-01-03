package com.simplesearch.entity;

import lombok.Data;

import java.util.Set;

@Data
public class IndexingRequest {
    private String path;
    private Set<String> mask;
    private Set<Character> separators;
}
