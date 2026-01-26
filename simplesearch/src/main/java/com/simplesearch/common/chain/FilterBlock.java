package com.simplesearch.common.chain;

import java.util.List;
import java.util.stream.Collectors;

import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.request.Query;

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
