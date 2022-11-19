package com.opensearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseHeader {
    @JsonProperty("Qtime")
    private Long Qtime;
    private Long normalizingTime;
    private boolean sorted;
    private Integer shardsUsed;
}
