package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;

import java.io.File;
import java.util.*;

public abstract class AbstractReader implements Reader {

    public Map<String, List<Document>> parseText(String text, File file, List<Character> regex) {
        List<String> words = Arrays.asList(text.split("\n")); // fixme
        Map<String, List<Document>> dict = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            String str = words.get(i);
            if (str.isEmpty()) continue;
            List<Document> documents = dict.getOrDefault(str, new ArrayList<>());
            documents.add(new Document(file.getAbsolutePath(), i, DocumentType.SIMPLE, null));
            dict.put(str, documents);
        }
        return dict;
    }
}
