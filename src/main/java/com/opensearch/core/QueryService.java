package com.opensearch.core;

import com.opensearch.core.trieutils.TriePrefixMatcher;
import com.opensearch.core.trieutils.TrieSearcher;
import com.opensearch.entity.LookupResult;

import java.util.List;

public class QueryService {
    private final TrieMap trieMap;
    private final TrieSearcher searcher;
    private final TriePrefixMatcher prefixMatcher;

    public QueryService(TrieMap trieMap) {
        this.trieMap = trieMap;
        prefixMatcher = new TriePrefixMatcher();
        searcher = new TrieSearcher();
    }

    public void save(String key, int metadataId) {
        trieMap.add(key.toLowerCase(), metadataId);
    }

    public List<LookupResult> find(String query, int distance, int count, boolean fuzziness) {
        return searcher.lookup(query.toLowerCase(), distance, count, trieMap.getRootInstance(), fuzziness);
    }

    public List<LookupResult> matchPrefix(String query, int distance, int count) {
        return prefixMatcher.matchPrefix(query.toLowerCase(), distance, count, trieMap.getRootInstance());
    }

    public int getMapSize() {
        return trieMap.getSize();
    }
}
