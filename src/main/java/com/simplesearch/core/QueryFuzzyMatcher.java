package com.simplesearch.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import com.simplesearch.core.trieutils.TrieUtils;
import com.simplesearch.model.internal.LookupResult;

public class QueryFuzzyMatcher {

    private final Trie trieMap;

    public QueryFuzzyMatcher(Trie trieMap) {
        this.trieMap = trieMap;
    }

    public List<LookupResult> lookup(String query, int distance, int count, boolean fuzziness) {
        MatchingResult result = fuzzySearch(query, distance, count, (mr, n) -> true);
        return result.found;
    }

    public MatchingResult fuzzySearch(String text, int maxEdits, int desiredCount, OnMatch onFind) {
        try {
            trieMap.getLock().lock();
            MatchingResult result = new MatchingResult();
            int[] initRow = initDist(text);

            Deque<State> stack = new ArrayDeque<>();
            stack.push(new State(trieMap.getRootInstance(), "", initRow));

            while (!stack.isEmpty() && result.count < desiredCount) {
                State st = stack.pop();
                TNode n = st.node;
                String pref = st.prefix;
                int[] row = st.row;

                if (n.isEnd && row[row.length - 1] <= maxEdits) {
                    result.found.add(new LookupResult(pref, n.serialIds));
                    result.count++;
                    if (!onFind.action(result, n)) {
                        break;
                    }
                }

                boolean prune = true;
                for (int d : row) {
                    if (d <= maxEdits) {
                        prune = false;
                        break;
                    }
                }
                if (prune)
                    continue;

                for (Map.Entry<Character, TNode> e : n.successors.entrySet()) {
                    int[] newRow = row;
                    for (char ch : e.getValue().sequence.toCharArray()) {
                        newRow = distRow(newRow, text, ch);
                    }
                    stack.push(new State(e.getValue(),
                            pref + e.getValue().sequence,
                            newRow));
                }
            }

            return result;
        } finally {
            trieMap.getLock().unlock();
        }
    }

    private static int[] initDist(String text) {
        int[] d = new int[text.length() + 1];
        for (int i = 0; i < d.length; ++i)
            d[i] = i;
        return d;
    }

    private static class State {
        TNode node;
        String prefix;
        int[] row;

        State(TNode n, String p, int[] r) {
            node = n;
            prefix = p;
            row = r;
        }
    }

    static class Result {
        String key;
        List<Integer> values;

        Result(String k, List<Integer> v) {
            key = k;
            values = v;
        }
    }

    private static class MatchingResult {
        int count = 0;
        final List<LookupResult> found = new ArrayList<>();
    }

    @FunctionalInterface
    public interface OnMatch {
        boolean action(MatchingResult lr, TNode node);
    }

    private static int[] distRow(int[] prev, String text, char c) {
        int m = text.length();
        int[] cur = new int[m + 1];
        cur[0] = prev[0] + 1;

        char nc = TrieUtils.norm(c);
        for (int i = 1; i <= m; ++i) {
            int cost = TrieUtils.norm(text.charAt(i - 1)) == nc ? 0 : 1;

            int del = cur[i - 1] + 1;
            int ins = prev[i] + 1;
            int sub = prev[i - 1] + cost;

            cur[i] = Math.min(Math.min(del, ins), sub);
        }
        return cur;
    }

}
