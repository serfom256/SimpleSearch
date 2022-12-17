package com.opensearch.util.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonReader implements Reader {

    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        JsonElement jsonElement = JsonParser.parseString(readJsonFile(file));
        HashMap<String, List<Document>> map = new HashMap<>();
        readJson(map, jsonElement, file.getAbsolutePath(), null);
        return map;
    }

    private void readJson(Map<String, List<Document>> map, JsonElement obj, String path, String key) {
        if (obj.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : obj.getAsJsonObject().asMap().entrySet()) {
                readJson(map, entry.getValue(), path, entry.getKey());
            }
        } else if (obj.isJsonArray()) {
            for (JsonElement element : obj.getAsJsonArray()) {
                readJson(map, element, path, key);
            }
        } else {
            String val = obj.getAsString().strip();
            if (val.length() == 0) return;
            Document document = new Document(path, null, DocumentType.JSON, key);
            List<Document> list = map.getOrDefault(val, new ArrayList<>());
            list.add(document);
            map.put(val, list);
        }
    }

    private String readJsonFile(File file) {
        try {
            return new String(Files.readAllBytes(Path.of(file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
