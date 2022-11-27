package com.opensearch.common.chain;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;

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
