package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;

import java.io.File;
import java.util.List;
import java.util.Map;

public class PathReader implements Reader {
    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        return null;
    }
}
