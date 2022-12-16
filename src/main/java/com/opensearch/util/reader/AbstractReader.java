package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;

import java.io.File;
import java.util.*;

public abstract class AbstractReader implements Reader {
    private static final Map<Character, String> regexMatch = new HashMap<>();

    static {
        regexMatch.put(' ', "\\s");
    }


    public Map<String, List<Document>> parseText(String text, File file, List<Character> regex) {
        List<String> words = Arrays.asList(text.split(buildRegex(regex)));
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

    private String buildRegex(List<Character> regex) {
        StringBuilder result = new StringBuilder();
        for (Character c : regex) {
            if (regexMatch.containsKey(c)) result.append(regexMatch.get(c));
            else result.append("\\").append(c);
            result.append("|");
        }
        return result.substring(0, result.length() - 1);
    }
}
