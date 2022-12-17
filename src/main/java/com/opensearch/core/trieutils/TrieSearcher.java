package com.opensearch.core.trieutils;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.trie.TNode;

import java.util.ArrayList;
import java.util.List;

public class TrieSearcher {

    public List<LookupResult> lookup(String input, int distance, int count, TNode root, boolean fuzziness) {
        String[] indexes = input.split(" ");
        SearchEntity result = new SearchEntity(count, distance, indexes, new ArrayList<>());
        int attempts = indexes.length;
        while (result.getWordPos() < indexes.length && attempts >= 0) {
            int pPos = result.getWordPos();
            int estimatedDistance = distance;
            if (fuzziness) {
                estimatedDistance = Math.min(TrieUtils.getFuzziness(result.getCurrent()), distance);
            }
            result.setTypos(estimatedDistance);
            fuzzyLookup(root, 0, estimatedDistance, result);
            if (pPos == result.getWordPos() && result.hasNextSequence()) {
                result.setCurrent(result.getNext());
            }
            attempts--;
        }
        return result.getResult();
    }

    private void fuzzyLookup(TNode start, int pos, int typos, SearchEntity entity) {
        if (typos < 0 || start == null || entity.isFounded()) return;
        if (start.isEnd && TrieUtils.distance(TrieUtils.getReversed(start), entity.getCurrent()) <= entity.getTypos()) {
            TrieUtils.collectForNode(entity, start);
        }
        if ((start.isEnd || (pos + typos) >= entity.getSearchedLength()) && entity.hasNextSequence()) {
            findNext(start, pos, typos, entity);
        }
        if (start.successors == null) return;
        for (TNode v : start.successors) {
            if (pos < entity.getSearchedLength() && v.element == entity.getCurrent().charAt(pos)) {
                fuzzyLookup(v, pos + 1, typos, entity);
            }
            fuzzyLookup(v, pos + 1, typos - 1, entity);
            fuzzyLookup(v, pos, typos - 1, entity);
            fuzzyLookup(start, pos + 1, typos - 1, entity);
            if (entity.isFounded()) return;
        }
    }


    private void findNext(TNode start, int pos, int typos, SearchEntity entity) {
        String next = entity.getNext();
        String curr = entity.getCurrent();
        int founded = entity.getResult().size();
        entity.setCurrent(entity.getCurrent() + " " + next);
        fuzzyLookup(start, pos, typos, entity);
        entity.setCurrent(curr);
        if (founded == entity.getResult().size()) {
            entity.setWordPos(entity.getWordPos() - 1);
        }
    }
}
