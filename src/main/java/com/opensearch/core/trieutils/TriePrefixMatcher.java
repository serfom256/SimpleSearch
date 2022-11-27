package com.opensearch.core.trieutils;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.trie.TNode;

import java.util.ArrayList;
import java.util.List;


public class TriePrefixMatcher {

    public List<LookupResult> matchPrefix(String input, int distance, int count, TNode curr) {
        return lookupForPrefix(input, distance, count, curr);
    }

    private List<LookupResult> lookupForPrefix(String input, int distance, int count, TNode curr) {
        int len = input.length() - 1;
        SearchEntity searchEntity = new SearchEntity(count, distance, input, new ArrayList<>());
        for (int i = 0; i <= len; i++) {
            char c = input.charAt(i);
            TNode next = curr.getNode(c);
            if (next == null) {
                len = i;
                break;
            }
            if (i == len && next.isEnd) TrieUtils.collectForNode(searchEntity, curr);
            curr = next;
        }
        return search(len, curr, searchEntity);
    }

    private List<LookupResult> search(int pos, TNode curr, SearchEntity entity) {
        for (int j = pos; j >= 0 && !entity.isFounded() && curr != null; j--, curr = curr.prev) {
            collectWordsFuzzy(curr, j, entity.getTypos(), entity);
        }
        return entity.getResult();
    }

    private void collectWordsFuzzy(TNode start, int pos, int typos, SearchEntity entity) {
        if (typos < 0 || entity.isFounded()) return;
        if (pos + typos >= entity.getToSearch().length() && start.prev != null) {
            TrieUtils.collectBranch(entity, start);
            if (entity.isFounded()) return;
        }
        for (TNode v : start.successors) {
            char k = v.element;
            if (pos < entity.getToSearch().length() && k == entity.getToSearch().charAt(pos)) {
                collectWordsFuzzy(v, pos + 1, typos, entity);
            } else {
                collectWordsFuzzy(v, pos + 1, typos - 1, entity);
                collectWordsFuzzy(v, pos, typos - 1, entity);
                collectWordsFuzzy(start, pos + 1, typos - 1, entity);
            }
            if (entity.isFounded()) return;
        }
    }
}
