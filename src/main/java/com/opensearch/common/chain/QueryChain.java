package com.opensearch.common.chain;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;

import java.util.List;

public abstract class QueryChain {

    private final QueryChain nextBlock;

    QueryChain(QueryChain nextBlock) {
        this.nextBlock = nextBlock;
    }

    public QueryChain getNext() {
        if (nextBlock == null) return new TerminationBlock(null);
        return nextBlock;
    }

    public abstract List<LookupResult> evaluate(List<LookupResult> resultList, Query query);
}
