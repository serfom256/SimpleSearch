package com.opensearch.entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TNode {
    public char element;
    public String seq;
    public boolean isEnd;
    public final TNode prev;
    public String metadata;
    public List<TNode> successors;

    public TNode(Character element, TNode prev) {
        this.element = element;
        this.prev = prev;
        this.isEnd = false;
    }

    public TNode(Character element, TNode prev, String seq) {
        this.element = element;
        this.prev = prev;
        this.seq = seq;
        this.isEnd = false;
    }

    public TNode() {
        this.isEnd = false;
        this.prev = null;
    }

    public TNode getNode(char c) {
        if (successors == null) return null;
        for (TNode node : successors) {
            if (node.element == c) return node;
        }
        return null;
    }

    public void addSuccessor(TNode node) {
        if (successors == null) successors = new CopyOnWriteArrayList<>();
        successors.add(node);
    }

    public int getSuccessorsSize() {
        if (successors == null) return 0;
        return successors.size();
    }

    public boolean isEmpty() {
        return seq == null || seq.length() == 0;
    }

}
