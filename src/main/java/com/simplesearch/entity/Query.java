package com.simplesearch.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Query {
    private String toSearch;
    private Integer count;
    private Integer distance;
    private boolean sort;
    private String groupBy;
    private boolean fuzziness;
    private Operator operator;
}
