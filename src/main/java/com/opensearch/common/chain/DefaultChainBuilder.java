package com.opensearch.common.chain;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;
import com.opensearch.entity.ResponseHeader;
import com.opensearch.entity.SearchResponse;

import java.util.List;

public class DefaultChainBuilder {

    public SearchResponse getQueryChain(Query query, List<LookupResult> resultList, long searchTime, int shards){
        SearchResponse response = new SearchResponse();
        SortBlock sortBlock = new SortBlock(null);
        FilterBlock filterBlock = new FilterBlock(sortBlock);
        List<LookupResult> evaluated = filterBlock.evaluate(resultList, query);


        ResponseHeader header = ResponseHeader
                .builder()
                .Qtime(System.currentTimeMillis() -  searchTime)
                .shardsUsed(shards)
                .sorted(query.isSort()).build();

        response.setResultList(evaluated);
        response.setHeader(header);
        return response;
    }
}
