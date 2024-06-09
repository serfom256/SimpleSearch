package com.simplesearch.model.response;


import lombok.Data;

import java.util.List;

import com.simplesearch.model.internal.LookupResult;

@Data
public class SearchResponse {
    private ResponseHeader header;
    private List<LookupResult> resultList;
}
