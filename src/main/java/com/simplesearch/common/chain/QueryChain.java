package com.simplesearch.common.chain;

import com.simplesearch.entity.LookupResult;
import com.simplesearch.entity.Query;

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
