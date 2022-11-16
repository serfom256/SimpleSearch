package com.opensearch.util.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
        HashMap<String,  List<Document>> map = new HashMap<>();
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
            Document document = new Document(path, null, DocumentType.JSON, null);
            List<Document> list = map.getOrDefault(key, new ArrayList<>());
            list.add(document);
            map.put(key, list);
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

//    @PostConstruct
//    public void main() {
//        Map<String, List<Document>> read = new JsonReader().read(new File("C:\\Users\\User\\Downloads\\workflow.json"), new ArrayList<>());
//        System.out.println(read);
//        read.forEach((a, b) -> System.out.println(a + " " + b));
//    }


}
