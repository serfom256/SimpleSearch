package com.opensearch.service;

import com.opensearch.core.balancer.LoadBalancer;
import com.opensearch.entity.IndexingRequest;
import com.opensearch.entity.IndexingResponse;
import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;
import com.opensearch.exceptions.PathNotFoundException;
import com.opensearch.util.ReaderCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Log4j2
@Component
public class FileReadingService {

    private final ReaderCommand readerCommand;
    private final LoadBalancer balancer;

    @Autowired
    public FileReadingService(LoadBalancer balancer) {
        this.balancer = balancer;
        readerCommand = new ReaderCommand();
    }

    public IndexingResponse read(IndexingRequest document) {
        File dataDir = new File(document.getPath());
        if (!dataDir.exists()) throw new PathNotFoundException(document.getPath());
        IndexingResponse indexingResponse = new IndexingResponse();
        long iTime = System.currentTimeMillis();
        Map<String, List<Document>> total = new HashMap<>();
        try {
            File path = new File(document.getPath());
            if (path.isFile()) {
                readFile(path, indexingResponse, new ArrayList<>(document.getSeparators()), total);
            }
            indexFilesRecursively(path, indexingResponse, document.getFilesType(), new ArrayList<>(document.getSeparators()), total);
        } catch (Exception e) {
            e.printStackTrace();
        }
        indexingResponse.setIndexingTime(System.currentTimeMillis() - iTime);
        balancer.createIndexesForCollection(total);
        return indexingResponse;
    }

    private void indexFilesRecursively(File path, IndexingResponse indexingResponse, Set<String> matched, List<Character> regex, Map<String, List<Document>> res) {
        File[] list = path.listFiles();
        if (matched.contains(getFileExtension(path))) {
            addToMap(res, createPathIndex(path));
            calcIndexedDocs(indexingResponse, 1, 1);
        }
        if (list == null) return;
        for (File f : list) {
            if (f.isDirectory()) {
                indexFilesRecursively(f, indexingResponse, matched, regex, res);
            } else if (f.isFile()) {
                if (matched.contains(getFileExtension(f))) {
                    readFile(f, indexingResponse, regex, res);
                }
                if (matched.contains("") && !getFileExtension(f).isEmpty()) {
                    addToMap(res, createPathIndex(f));
                    calcIndexedDocs(indexingResponse, 1, 1);
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

    private void readFile(File f, IndexingResponse indexingResponse, List<Character> regex, Map<String, List<Document>> result) {
        Map<String, List<Document>> read = readerCommand.getReaderByExtension(getFileExtension(f)).read(f, regex);
        addToMap(result, read);
        calcIndexedDocs(indexingResponse, 1, read.size());
    }

    private void calcIndexedDocs(IndexingResponse indexingResponse, int docsIndexed, int entitiesIndexed) {
        indexingResponse.setEntitiesIndexed(indexingResponse.getEntitiesIndexed() + entitiesIndexed);
        indexingResponse.setDocumentsIndexed(indexingResponse.getDocumentsIndexed() + docsIndexed);
    }

    private Map<String, List<Document>> createPathIndex(File file) {
        Map<String, List<Document>> map = new HashMap<>();
        map.computeIfAbsent(file.getName(), l -> new ArrayList<>()).add(new Document(file.getAbsolutePath(), null, DocumentType.PATH, null));
        return map;
    }

    private String getFileExtension(File file) {
        if (file.isDirectory()) return "";
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".") + 1;
        if (lastIndexOf == 0) return "";
        return name.substring(lastIndexOf);
    }
}
