package com.simplesearch.util.reader;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.simplesearch.model.internal.document.Document;

public interface Reader {
    Map<String, List<Document>> read(File file, List<Character> regex);
}
