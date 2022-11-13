package com.opensearch.util.reader;

import com.opensearch.entity.ObjectMetadata;

import java.util.List;

public interface Reader {
    List<ObjectMetadata> read(String file, List<Character> regex);
}
