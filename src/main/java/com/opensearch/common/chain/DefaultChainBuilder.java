package com.opensearch.common.chain;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;
import com.opensearch.entity.ResponseHeader;
import com.opensearch.entity.SearchResponse;

import java.util.List;

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
