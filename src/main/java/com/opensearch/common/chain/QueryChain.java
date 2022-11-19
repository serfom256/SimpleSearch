package com.opensearch.common.chain;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;

import java.util.List;

public interface QueryChain {

    QueryChain evaluate(List<LookupResult> resultList, QueryChain next, Query query);

    List<LookupResult> getResult();
}
