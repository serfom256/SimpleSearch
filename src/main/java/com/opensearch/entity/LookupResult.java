package com.opensearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class LookupResult {
    private String key;
    @JsonIgnore
    private List<Integer> serializedIds;
    private ObjectMetadata metadata;
}
