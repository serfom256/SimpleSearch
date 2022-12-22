package com.simplesearch.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Query {
    private final String toSearch;
    private final Integer count;
    private final Integer distance;
    private final boolean sort;
    private final String groupBy;
    private final boolean fuzziness;
    private final Operator operator;
}
