package com.opensearch.core.trieutils;

import com.opensearch.entity.LookupResult;
import com.opensearch.entity.trie.TNode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchEntity {
    private final int count;
    @Getter
    @Setter
    private int typos;
    private final String[] toSearch;
    @Getter
    @Setter
    private String current;
    private final List<LookupResult> founded;
    private final Set<TNode> set;
    @Getter
    @Setter
    private int wordPos;


    public SearchEntity(int count, int typos, String[] toSearch, List<LookupResult> founded) {
        this.count = count;
        this.typos = typos;
        this.toSearch = toSearch;
        this.founded = founded;
        this.current = toSearch[0];
        set = new HashSet<>();
        wordPos = 0;
    }

    public String getNext() {
        return toSearch[++wordPos];
    }

    public boolean hasNextSequence() {
        return wordPos + 1 < toSearch.length;
    }

    public int getSearchedLength() {
        return current.length();
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
