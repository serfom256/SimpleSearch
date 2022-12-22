package com.simplesearch.util.reader;

import com.simplesearch.entity.document.Document;
import com.simplesearch.entity.document.DocumentType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathReader implements Reader {
    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        Map<String, List<Document>> map = new HashMap<>();
        if (file.isFile()) {
            map.computeIfAbsent(file.getName(), l -> new ArrayList<>()).add(new Document(file.getAbsolutePath(), null, DocumentType.PATH, null));
        } else {
            indexFiles(file, map);
        }
        return map;
    }

    private void indexFiles(File dir, Map<String, List<Document>> map) {
        File[] fList = dir.listFiles();
        if (fList == null) return;
        for (File file : fList) {
            if (file.isFile()) {
                map.computeIfAbsent(file.getName(), l -> new ArrayList<>()).add(new Document(file.getAbsolutePath(), null, DocumentType.PATH, null));
            } else if (file.isDirectory()) {
                map.computeIfAbsent(file.getName(), l -> new ArrayList<>()).add(new Document(file.getAbsolutePath(), null, DocumentType.PATH, null));
                indexFiles(file, map);
            }
        }
    }

}
