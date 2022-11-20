package com.opensearch.util.reader;


import com.opensearch.entity.document.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class FileReader extends AbstractReader {

    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        String fileContent;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
        return parseText(fileContent, file, regex);
    }
}
