package com.opensearch.entity;

import lombok.Data;

@Data
public class IndexingResponse {
    private long indexingTime;
    private int documentsIndexed;
    private int entitiesIndexed;
}
