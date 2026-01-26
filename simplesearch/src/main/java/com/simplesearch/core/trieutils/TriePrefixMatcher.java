package com.simplesearch.core.trieutils;

import java.util.Collections;
import java.util.List;

import com.simplesearch.core.TNode;
import com.simplesearch.model.internal.LookupResult;

public class TriePrefixMatcher {

    public List<LookupResult> matchPrefix(String input, int distance, int count, TNode curr) {
        int fuzziness = TrieUtils.getFuzziness(input);
        int estimatedDistance = Math.min(distance, fuzziness);
        return Collections.emptyList();
    }

}
