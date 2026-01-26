package com.simplesearch.common.chain;

import java.util.List;

import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.request.Query;
import com.simplesearch.model.response.ResponseHeader;
import com.simplesearch.model.response.SearchResponse;

public class DefaultChainBuilder {

    private final QueryChain initialBlock;

    public DefaultChainBuilder() {
        initialBlock = buildDefaultChain();
    }

    public SearchResponse executeQueryChain(Query query, List<LookupResult> resultList, long searchTime, int shards) {
        SearchResponse response = new SearchResponse();
        List<LookupResult> evaluated = initialBlock.evaluate(resultList, query);
        ResponseHeader header = ResponseHeader
                .builder()
                .qTime(System.currentTimeMillis() - searchTime)
                .shardsUsed(shards)
                .founded(evaluated.size())
                .sorted(query.isSort()).build();

        response.setResultList(evaluated);
        response.setHeader(header);
        return response;
    }


    public QueryChain buildDefaultChain() {
//        GroupByBlock groupBy = new GroupByBlock(null);
        FilterBlock filter = new FilterBlock(null);
        return new SortBlock(filter);
    }
}
