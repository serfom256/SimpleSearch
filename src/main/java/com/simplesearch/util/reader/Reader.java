package com.simplesearch.util.reader;

import com.simplesearch.entity.document.Document;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Reader {
    Map<String, List<Document>> read(File file, List<Character> regex);
}
