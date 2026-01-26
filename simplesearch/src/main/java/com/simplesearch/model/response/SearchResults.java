package com.simplesearch.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.simplesearch.model.internal.LookupResult;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResults {
    private String shardName;
    private int recursionCalls;
    private List<LookupResult> lookupResults;
}
