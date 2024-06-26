package com.simplesearch.common.chain;

import java.util.Comparator;
import java.util.List;

import com.simplesearch.model.internal.LookupResult;
import com.simplesearch.model.request.Query;

public class SortBlock extends QueryChain{

    SortBlock(QueryChain nextBlock) {
        super(nextBlock);
    }


    @Override
    public List<LookupResult>  evaluate(List<LookupResult> resultList, Query query) {
        if(query.isSort()) resultList.sort(Comparator.comparingInt(a -> distance(a.getKey(), query.getToSearch())));
        return getNext().evaluate(resultList, query);
    }


    private int distance(String s1, String s2) {
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
