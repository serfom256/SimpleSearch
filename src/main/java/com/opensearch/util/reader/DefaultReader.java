package com.opensearch.util.reader;


import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;


public class DefaultReader implements Reader {
    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        try {
            String readerFile = Files.readString(file.toPath());
            List<String> parts = Arrays.stream(readerFile.split(" ")).toList();
            Map<String, List<Document>> dict = new HashMap<>();
            for (int i = 0; i < parts.size(); i++) {
                List<Document> tempListDoc = dict.getOrDefault(parts.get(i), new ArrayList<>());
                tempListDoc.add(new Document(file.getAbsolutePath(), i, DocumentType.SIMPLE, null));
                dict.put(parts.get(i), tempListDoc);
            }
            return dict;
        } catch (Exception e) {
            System.out.println(e);
            return Collections.emptyMap();
        }
    }
}
