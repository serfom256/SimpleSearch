package com.opensearch.util.reader;

import com.opensearch.entity.document.JsonDocument;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JsonReader implements Reader{
    @Override
    public Map<String, JsonDocument> read(File file, List<Character> regex) {
        return null;
    }
}
