package com.opensearch.core.trieutils;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.trie.TNode;

import java.util.ArrayList;
import java.util.List;

public class TrieSearcher {

    public List<LookupResult> lookup(String input, int distance, int count, TNode root) {
        TrieUtils.checkSearchConstraints(input, distance);
        SearchEntity result = new SearchEntity(count, distance, input, new ArrayList<>());
        fuzzyLookup(root, 0, distance, result);
        return result.getResult();
    }

    private void fuzzyLookup(TNode start, int pos, int typos, SearchEntity entity) {
        if (typos < 0 || start == null || entity.isFounded()) return;
        if (start.isEnd && TrieUtils.distance(TrieUtils.getReversed(start), entity.getToSearch()) <= entity.getTypos()) {
            TrieUtils.collectForNode(entity, start);
        }
        if (start.successors == null) return;
        for (TNode v : start.successors) {
            if (pos < entity.getSearchedLength() && v.element == entity.getToSearch().charAt(pos)) {
                fuzzyLookup(v, pos + 1, typos, entity);
            }
            fuzzyLookup(v, pos + 1, typos - 1, entity);
            fuzzyLookup(v, pos, typos - 1, entity);
            fuzzyLookup(start, pos + 1, typos - 1, entity);
            if (entity.isFounded()) return;
        }
    }
}
