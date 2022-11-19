package com.opensearch.util.reader;


import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;

import java.io.File;
import java.nio.file.Files;
import java.util.*;


public class FileReader implements Reader {

    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        String fileContent;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
        List<String> words = Arrays.asList(fileContent.split(" "));
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
