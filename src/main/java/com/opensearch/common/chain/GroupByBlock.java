package com.opensearch.common.chain;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;

import java.util.List;

public class GroupByBlock implements QueryChain{
    @Override
    public QueryChain evaluate(List<LookupResult> resultList, QueryChain next, Query query) {
        return null;
    }

    @Override
    public List<LookupResult> getResult() {
        return null;
    }
}
