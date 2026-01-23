package com.simplesearch.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.simplesearch.core.trieutils.TrieUtils;

public class Trie {

    private final TNode root = new TNode();
    private long size = 0L;
    private final Lock lock = new ReentrantLock();

    public void add(String key, Integer serialId) {
        lock.lock();
        try {
            TNode n = root;
            while (!key.isEmpty()) {
                char c = key.charAt(0);
                TNode next = n.successors.get(c);
                if (next == null) {
                    TNode newSuccessor = new TNode();
                    newSuccessor.sequence = key;
                    newSuccessor.serialIds.add(serialId);
                    newSuccessor.isEnd = true;
                    n.successors.put(c, newSuccessor);
                    size++;
                    return;
                }

                int commonLen = lcp(key, next.sequence);

                if (commonLen == next.sequence.length()) {
                    key = key.substring(commonLen);
                    n = next;
                    continue;
                }
                if (commonLen == 0)
                    return;

                String suffix = next.sequence.substring(commonLen);

                TNode internal = new TNode();
                internal.sequence = next.sequence.substring(0, commonLen);
                internal.successors.put(suffix.charAt(0), next);

                next.sequence = suffix;
                n.successors.put(c, internal);
                key = key.substring(commonLen);

                if (key.isEmpty()) {
                    internal.isEnd = true;
                    internal.serialIds.add(serialId);
                    size++;
                    return;
                }

                TNode newLeaf = new TNode();
                newLeaf.sequence = key;
                newLeaf.serialIds.add(serialId);
                newLeaf.isEnd = true;
                internal.successors.put(key.charAt(0), newLeaf);
                size++;
                return;
            }

            n.isEnd = true;
            size++;
            n.serialIds.add(serialId);
        } finally {
            lock.unlock();
        }
    }

    private static int lcp(String a, String b) {
        int i = 0;
        while (i < a.length() && i < b.length()
                && TrieUtils.norm(a.charAt(i)) == TrieUtils.norm(b.charAt(i))) {
            ++i;
        }
        return i;
    }

    public void delete(String key) {
        lock.lock();
        try {
            Deque<Pair> stack = new ArrayDeque<>();
            TNode n = root;

            while (!key.isEmpty()) {
                char c = key.charAt(0);
                TNode successor = n.successors.get(c);
                if (successor == null)
                    return;

                stack.push(new Pair(n, c));
                int lp = lcp(key, successor.sequence);
                if (lp == 0)
                    return;
                if (lp == successor.sequence.length()) {
                    key = key.substring(lp);
                    n = successor;
                } else
                    return;
            }

            if (n.successors.isEmpty()) {
                while (!stack.isEmpty()) {
                    Pair p = stack.pop();
                    p.prev.successors.remove(p.successor);
                    if (!p.prev.successors.isEmpty() || p.prev.isEnd)
                        break;
                }
            } else {
                size--;
                n.isEnd = false;
                n.serialIds.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    private static class Pair {
        TNode prev;
        char successor;

        Pair(TNode p, char c) {
            prev = p;
            successor = c;
        }
    }

    protected Lock getLock() {
        return lock;
    }

    protected TNode getRootInstance() {
        return root;
    }

    protected long getSize() {
        return size;
    }
}