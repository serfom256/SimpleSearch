package com.opensearch.core;


import com.opensearch.entity.trie.TNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class TrieMap {

    private final TNode root;
    private final AtomicInteger pairs;
    private final Map<Character, RootNode> rootNodes;

    private static class RootNode {

        private final Lock lock;
        private TNode node;

        private RootNode(TNode node) {
            this.node = node;
            this.lock = new ReentrantLock();
        }
    }

    public TrieMap() {
        this.root = new TNode();
        this.rootNodes = new HashMap<>(128);
        this.pairs = new AtomicInteger();
    }

    /**
     * Appends the new key-values pair to the TrieMap of the specified key and the specified value
     *
     * @throws IllegalArgumentException if the specified key or value is null
     * @throws IllegalArgumentException if the length of the specified key is equals 0
     */
    public void add(String key, Integer serializedId) {
        char f1 = key.charAt(0);
        RootNode rn1 = rootNodes.get(f1);
        if (rn1 == null) {
            synchronized (this) {
                rn1 = rootNodes.get(f1);
                TNode keyNode;
                if (rn1 == null) {
                    keyNode = insertToRoot(key);
                } else {
                    keyNode = putSequence(key);
                    if (!keyNode.isEnd) pairs.incrementAndGet();
                }
                keyNode.addSerializedId(serializedId);
                keyNode.isEnd = true;
            }
            return;
        }
        rn1.lock.lock();
        TNode keyNode = putSequence(key);
        if (!keyNode.isEnd) pairs.incrementAndGet();
        keyNode.addSerializedId(serializedId);
        keyNode.isEnd = true;
        rn1.lock.unlock();
    }

    private TNode putSequence(String sequence) {
        RootNode rn = rootNodes.get(sequence.charAt(0));
        TNode curr = rn.node;
        for (int i = 1; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            TNode next = curr.getNode(c);
            if (next == null) {
                String sq = sequence.substring(i);
                if (Objects.equals(curr.seq, sq)) return curr;
                return buildTree(curr, sq);
            }
            curr = next;
        }
        return splitTree(curr);
    }


    private TNode splitTree(TNode node) {
        if (!node.isEmpty()) {
            TNode prev = node.prev;
            if (prev == null) {
                prev = root;
            }
            prev.successors.remove(node);
            TNode curr = new TNode(node.element, prev);
            prev.addSuccessor(curr);
            TNode toNext = new TNode(node.seq.charAt(0), curr, node.seq.substring(1));
            toNext.isEnd = node.isEnd;
            toNext.serializedIds = node.serializedIds;
            curr.addSuccessor(toNext);
            if (prev == root) {
                rootNodes.get(node.element).node = curr;
            }
            return curr;
        }
        return node;
    }

    private TNode insertToRoot(String seq) {
        char c = seq.charAt(0);
        TNode curr = new TNode(c, null, seq.substring(1));
        this.rootNodes.put(c, new RootNode(curr));
        root.addSuccessor(curr);
        return curr;
    }


    private TNode buildTree(TNode node, String seq) {
        if (node.seq == null) {
            TNode newNode = new TNode(seq.charAt(0), node, seq.substring(1));
            node.addSuccessor(newNode);
            return newNode;
        }
        String nodeSeq = node.seq;
        node.seq = null;
        boolean isEnd = node.isEnd;
        node.isEnd = false;
        List<Integer> ids = node.serializedIds;
        node.serializedIds = null;
        int pos = 0, len = Math.min(seq.length(), nodeSeq.length());
        while (pos < len && seq.charAt(pos) == nodeSeq.charAt(pos)) {
            TNode newNode = new TNode(seq.charAt(pos), node);
            node.addSuccessor(newNode);
            node = newNode;
            pos++;
        }
        if (pos < len) {
            TNode newNode = new TNode(nodeSeq.charAt(pos), node, nodeSeq.substring(pos + 1));
            TNode inserted = new TNode(seq.charAt(pos), node, seq.substring(pos + 1));
            newNode.isEnd |= isEnd;
            newNode.serializedIds = ids;
            node.addSuccessor(newNode);
            node.addSuccessor(inserted);
            return inserted;
        } else if (pos < nodeSeq.length()) {
            TNode newNode = new TNode(nodeSeq.charAt(pos), node, nodeSeq.substring(pos + 1));
            newNode.isEnd |= isEnd;
            newNode.serializedIds = ids;
            node.addSuccessor(newNode);
            return node;
        } else if (pos < seq.length()) {
            TNode newNode = new TNode(seq.charAt(pos), node, seq.substring(pos + 1));
            node.isEnd |= isEnd;
            node.serializedIds = ids;
            node.addSuccessor(newNode);
            return newNode;
        }
        return node;
    }

    public int getSize() {
        return pairs.get();
    }

    public void clear() {
        synchronized (this) {
            rootNodes.clear();
            if (root.successors != null) root.successors.clear();
            root.isEnd = false;
            pairs.set(0);
        }
    }

    // todo add return root with root lock acquiring
    public TNode getRootInstance() {
        return root;
    }

    /**
     * Helps to print all entries from the TrieMap
     */
    private void toStringHelper(StringBuilder res, TNode node) {
        if (node.successors == null) return;
        for (TNode c : node.successors) {
            if (c.isEnd) {
                res.append(c.serializedIds).append(", ");
            }
            toStringHelper(res, c);
        }
    }

    //    @Override
    public String totring() {
        if (pairs.get() == 0) return "[]";
        StringBuilder s = new StringBuilder("[");
        toStringHelper(s, root);
        return s.replace(s.length() - 2, s.length(), "]").toString();
    }

}
