package com.simplesearch.common.chain;

import java.util.List;

import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.request.Query;

public class TerminationBlock extends QueryChain {

    TerminationBlock(QueryChain nextBlock) {
        super(nextBlock);
    }

    @Override
    public List<LookupResult> evaluate(List<LookupResult> resultList, Query query) {
        return resultList;
    }
}
