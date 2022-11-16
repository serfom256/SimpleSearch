package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Reader {
    Map<String, ? extends Document> read(File file, List<Character> regex);
}
