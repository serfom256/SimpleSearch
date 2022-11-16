package com.opensearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexingResponse {
    private long indexingTime;
    private int documentsIndexed;
    private int entitiesIndexed;
}
