package com.simplesearch.core;

import com.simplesearch.core.trieutils.TriePrefixMatcher;
import com.simplesearch.model.internal.LookupResult;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class QueryService {
    private final Trie trieMap;
    private final QueryFuzzyMatcher searcher;
    private final TriePrefixMatcher prefixMatcher;

    public QueryService(Trie trieMap) {
        this.trieMap = trieMap;
        prefixMatcher = new TriePrefixMatcher();
        searcher = new QueryFuzzyMatcher(trieMap);
    }

    public void save(String key, int metadataId) {
        if (key.length() == 0) {
            log.warn("Skipping key with length 0");
            return;
        }
        trieMap.add(key.toLowerCase(), metadataId);
    }

    public List<LookupResult> find(String query, int distance, int count, boolean fuzziness) {
        return searcher.lookup(query.toLowerCase(), distance, count, fuzziness);
    }

    public List<LookupResult> matchPrefix(String query, int distance, int count) {
        return prefixMatcher.matchPrefix(query.toLowerCase(), distance, count, trieMap.getRootInstance());
    }

    public int getMapSize() {
        return (int)trieMap.getSize();
    }
}
