package com.opensearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseHeader {
    @JsonProperty("Qtime")
    private Long Qtime;
    private Long normalizingTime;
    private boolean sorted;
    private Integer shardsUsed;
}
