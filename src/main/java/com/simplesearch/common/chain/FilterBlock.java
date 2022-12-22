package com.simplesearch.common.chain;

import com.simplesearch.entity.LookupResult;
import com.simplesearch.entity.Query;

import java.util.List;
import java.util.stream.Collectors;

public class FilterBlock extends QueryChain {

    FilterBlock(QueryChain nextBlock) {
        super(nextBlock);
    }

    @Override
    public List<LookupResult>  evaluate(List<LookupResult> resultList, Query query) {
        resultList = resultList.stream().limit(query.getCount()).collect(Collectors.toList());
        return getNext().evaluate(resultList, query);
    }
}
