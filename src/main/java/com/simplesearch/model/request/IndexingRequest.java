package com.simplesearch.model.request;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class IndexingRequest {
    private String path;
    private Set<String> mask;
    private Set<Character> separators;
}
