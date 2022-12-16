package com.opensearch.core.trieutils;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.trie.TNode;

import java.util.ArrayList;

class TrieUtils {

    private TrieUtils() {
    }

    public static void collectForNode(SearchEntity entity, TNode node) {
        if (!node.isEnd || entity.hasNode(node)) return;
        LookupResult lookupResult = new LookupResult();
        lookupResult.setKey(getReversed(node));
        lookupResult.setSerializedIds(node.serializedIds);
        entity.addEntry(lookupResult);
        entity.memorize(node);
    }

    public static void collectBranch(SearchEntity entity, TNode node) {
        collectForNode(entity, node, new StringBuilder(getReversed(node)));
    }

    private static void collectForNode(SearchEntity entity, TNode node, StringBuilder prefix) {
        if (node == null || entity.isFounded() || entity.hasNode(node)) return;
        entity.memorize(node);
        if (node.isEnd) {
            LookupResult result = new LookupResult();
            result.setKey(prefix.toString());
            result.setSerializedIds(new ArrayList<>(node.serializedIds));
            entity.addEntry(result);
        }
        if (node.successors == null) return;
        for (TNode curr : node.successors) {
            if (entity.isFounded()) return;
            prefix.append(curr.element);
            collectForNode(entity, node, prefix);
            prefix.delete(prefix.length() - 1, prefix.length());
        }
    }


    public static String getReversed(TNode node) {
        StringBuilder prefix = new StringBuilder();
        String temp = node.seq;
        while (node != null) {
            prefix.append(node.element);
            node = node.prev;
        }
        prefix.reverse();
        if (temp != null) prefix.append(temp);
        return prefix.toString();
    }

    public static void checkSearchConstraints(String input, int distance) {
        if (input == null) throw new IllegalArgumentException();
        if (input.length() <= 1 || input.length() <= distance) {
            throw new IllegalArgumentException("Input length must be more than specified distance");
        }
    }

    public static int getFuzziness(String s) {
        int fuzziness = 0;
        for (int i = 1; i < s.length(); i *= 2 + 2) {
            fuzziness++;
        }
        return fuzziness;
    }

    public static int distance(String s1, String s2) {
        int len1 = s1.length(), len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int a = 0;
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) ++a;
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + a, Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[len1][len2];
    }
}
