package com.simplesearch.common.chain;

import com.simplesearch.entity.LookupResult;
import com.simplesearch.entity.Query;

import java.util.List;

public class TerminationBlock extends QueryChain {

    TerminationBlock(QueryChain nextBlock) {
        super(nextBlock);
    }

    @Override
    public List<LookupResult> evaluate(List<LookupResult> resultList, Query query) {
        return resultList;
    }
}
