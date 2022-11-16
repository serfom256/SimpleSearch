package com.opensearch.util.reader;

import com.google.gson.Gson;
import com.opensearch.entity.document.Document;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonReader implements Reader {

    private final Gson gson;

    public JsonReader() {
        gson = new Gson();
    }

    @Override
    public Map<String, Document> read(File file, List<Character> regex) {
        return Collections.emptyMap();
    }
}
