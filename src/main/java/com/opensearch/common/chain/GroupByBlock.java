package com.opensearch.common.chain;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.Query;
import com.opensearch.entity.document.Document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupByBlock extends QueryChain {

    GroupByBlock(QueryChain nextBlock) {
        super(nextBlock);
    }

    @Override
    public List<LookupResult> evaluate(List<LookupResult> resultList, Query query) { // fixme fix grouping algorithm with LookupResponseDto
        Map<String, List<LookupResult>> grouped = new LinkedHashMap<>();
        for (LookupResult res : resultList) {
            for (Document document : res.getMetadata()) {
                grouped.computeIfAbsent(document.getPath(), k -> new ArrayList<>()).add(res);
            }
        }
        List<LookupResult> groupedResults = new ArrayList<>(grouped.size() + 1);
        grouped.forEach((k, v) -> {
                    if (k != null) groupedResults.addAll(v);
                }
        );
        List<LookupResult> nullable = grouped.get(null);
        if (nullable != null) groupedResults.addAll(nullable);
        return groupedResults;
    }
}
