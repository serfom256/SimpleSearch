package com.simplesearch.service.files;

import com.simplesearch.exceptions.PathNotFoundException;
import com.simplesearch.model.internal.IndexingDTO;
import com.simplesearch.model.internal.document.Document;
import com.simplesearch.model.internal.document.DocumentType;
import com.simplesearch.model.request.IndexingRequest;
import com.simplesearch.util.ReaderCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class DataReadingService {

    private final ReaderCommand readerCommand;

    @Autowired
    public DataReadingService() {
        this.readerCommand = new ReaderCommand();
    }

    public IndexingDTO collectEntities(IndexingRequest request) {
        File dataDir = new File(request.getPath());
        if (!dataDir.exists()) {
            throw new PathNotFoundException(request.getPath());
        }
        IndexingDTO indexingResponse = new IndexingDTO(0, 0, new HashMap<>());
        try {
            File path = new File(request.getPath());
            if (path.isFile()) {
                readFile(path, indexingResponse, new ArrayList<>(request.getSeparators()));
            }
            indexFilesRecursively(path, indexingResponse, request.getMask(), new ArrayList<>(request.getSeparators()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return indexingResponse;
    }

    private void indexFilesRecursively(File path, IndexingDTO dto, Set<String> matched, List<Character> regex) {
        File[] list = path.listFiles();
        if (matched.contains(getFileExtension(path))) {
            addToMap(dto.getTotal(), createPathIndex(path));
            calcIndexedDocs(dto, 1, 1);
        }
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                indexFilesRecursively(f, dto, matched, regex);
            } else if (f.isFile()) {
                if (matched.contains(getFileExtension(f))) {
                    readFile(f, dto, regex);
                }
                if (matched.contains("") && !getFileExtension(f).isEmpty()) {
                    addToMap(dto.getTotal(), createPathIndex(f));
                    calcIndexedDocs(dto, 1, 1);
                }
            }
        }
    }

    private void addToMap(Map<String, List<Document>> res, Map<String, List<Document>> toAppend) {
        for (Map.Entry<String, List<Document>> entry : toAppend.entrySet()) {
            List<Document> documents = res.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
            for (Document document : entry.getValue()) {
                if (!documents.contains(document)) {
                    documents.add(document);
                }
            }
        }
    }

    private void readFile(File f, IndexingDTO response, List<Character> regex) {
        Map<String, List<Document>> read = readerCommand.getReaderByExtension(getFileExtension(f)).read(f, regex);
        addToMap(response.getTotal(), read);
        calcIndexedDocs(response, 1, read.size());
    }

    private void calcIndexedDocs(IndexingDTO response, int docsIndexed, int entitiesIndexed) {
        response.setEntitiesIndexed(response.getEntitiesIndexed() + entitiesIndexed);
        response.setDocumentsIndexed(response.getDocumentsIndexed() + docsIndexed);
    }

    private Map<String, List<Document>> createPathIndex(File file) {
        Map<String, List<Document>> map = new HashMap<>();
        map.computeIfAbsent(file.getName(), l -> new ArrayList<>())
                .add(new Document(file.getAbsolutePath(), null, DocumentType.PATH, null));
        return map;
    }

    private String getFileExtension(File file) {
        if (file.isDirectory())
            return "";
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".") + 1;
        if (lastIndexOf == 0)
            return "";
        return name.substring(lastIndexOf);
    }
}
