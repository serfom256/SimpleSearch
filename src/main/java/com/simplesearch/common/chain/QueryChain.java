package com.simplesearch.common.chain;

import java.util.List;

import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.request.Query;

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
