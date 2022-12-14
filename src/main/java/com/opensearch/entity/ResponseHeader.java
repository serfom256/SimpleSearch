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
    @JsonProperty(value = "Qtime")
    private Long qTime;
    private Long normalizingTime;
    private boolean sorted;
    private Integer founded;
    private Integer shardsUsed;
}
