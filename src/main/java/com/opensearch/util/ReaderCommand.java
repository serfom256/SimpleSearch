package com.opensearch.util;

import com.opensearch.util.reader.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReaderCommand {

    private final Map<String, Reader> readers;
    private final Reader defaultReader;

    public ReaderCommand() {
        readers = new HashMap<>();
        readers.put("docx", new DocxReader());
        readers.put("doc", new DocxReader());
        readers.put("pdf", new PdfReader());
        readers.put("json", new JsonReader());
        readers.put("", new PathReader());
        readers.put(null, new DefaultReader());
        defaultReader = new DefaultReader();
    }

    public Reader getReaderByExtension(String fileExtension) {
        Reader reader = readers.get(fileExtension);
        if (reader == null) return defaultReader;
        return reader;
    }
}
