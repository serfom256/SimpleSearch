package com.opensearch.entity;


import lombok.Data;

@Data
public class Query {
    private String toSearch;
    private Integer count;
    private Integer distance;
    private boolean sort;
}
