package com.simplesearch.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexingResponse {
    private String session;
    private long indexingTime;
    private int documentsIndexed;
    private long entitiesIndexed;
}
