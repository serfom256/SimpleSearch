package com.opensearch.util.reader;

import com.opensearch.entity.ObjectMetadata;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Reader {
    Map<String, ObjectMetadata> read(File file, List<Character> regex);
}
