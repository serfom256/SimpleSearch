package com.simplesearch.entity;


import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private ResponseHeader header;
    private List<LookupResult> resultList;
}
