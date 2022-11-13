package com.opensearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class LookupResult {
    private String key;
    @JsonIgnore
    private Integer serializedId;
    private ObjectMetadata metadata;
}
