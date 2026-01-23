package com.simplesearch.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TNode {
    protected String sequence = "";
    protected List<Integer> serialIds = new ArrayList<>();
    protected boolean isEnd = false;
    protected Map<Character, TNode> successors = new HashMap<>();
}
