package com.simplesearch.core;

import com.simplesearch.core.trieutils.TriePrefixMatcher;
import com.simplesearch.core.trieutils.TrieSearcher;
import com.simplesearch.model.internal.LookupResult;

import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
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
        if (key.length() == 0) {
            log.warn("Skipping key with length 0");
            return;
        }
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
