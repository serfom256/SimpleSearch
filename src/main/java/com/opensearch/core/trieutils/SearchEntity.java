package com.opensearch.core.trieutils;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.trie.TNode;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchEntity {
    private final int count;
    @Getter
    private final int typos;
    @Getter
    private final String toSearch;
    private final List<LookupResult> founded;
    private final Set<TNode> set;


    public SearchEntity(int count, int typos, String toSearch, List<LookupResult> founded) {
        this.count = count;
        this.typos = typos;
        this.toSearch = toSearch;
        this.founded = founded;
        set = new HashSet<>();
    }

    public int getSearchedLength() {
        return toSearch.length();
    }

    public boolean isFounded() {
        return count <= founded.size();
    }

    public List<LookupResult> getResult() {
        return founded;
    }

    public void addEntry(LookupResult result) {
        founded.add(result);
    }

    public void memorize(TNode result) {
        set.add(result);
    }

    public boolean hasNode(TNode node) {
        return set.contains(node);
    }
}
