package com.simplesearch.core;

import com.simplesearch.core.trieutils.TriePrefixMatcher;
import com.simplesearch.model.internal.LookupResult;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class QueryService {
    private final Trie trie;
    private final QueryFuzzyMatcher searcher;
    private final TriePrefixMatcher prefixMatcher;

    public QueryService(Trie trieMap) {
        this.trie = trieMap;
        prefixMatcher = new TriePrefixMatcher();
        searcher = new QueryFuzzyMatcher(trieMap);
    }

    public void save(String key, int idx) {
        if (key.length() == 0) {
            log.warn("Skipping key with length 0");
            return;
        }
        trie.add(key, idx);
    }

    public List<LookupResult> find(String query, int distance, int count, boolean fuzziness) {
        return searcher.lookup(query, distance, count, fuzziness);
    }

    public List<LookupResult> matchPrefix(String query, int distance, int count) {
        return prefixMatcher.matchPrefix(query, distance, count, trie.getRootInstance());
    }

    public int getMapSize() {
        return (int) trie.getSize();
    }
}
