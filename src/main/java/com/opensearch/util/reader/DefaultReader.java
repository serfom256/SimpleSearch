package com.opensearch.util.reader;


import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DefaultReader extends AbstractReader {

    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
        return parseText(fileContent.toString(), file, regex, DocumentType.PLAIN_TEXT);
    }
}
