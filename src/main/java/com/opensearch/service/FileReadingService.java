package com.opensearch.service;

import com.opensearch.core.LoadBalancer;
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
        File f = new File(document.getPath());
        long iTime = System.currentTimeMillis();
        if (f.isFile()) {
            Map<String, List<Document>> read = readerCommand.getReaderByExtension(getFileExtension(f)).read(f, new ArrayList<>());
            setIndexed(indexingResponse, 1, read.size() + 1);
            balancer.createIndex(createPathIndex(f));
            balancer.createIndex(read);
        } else {
            indexFilesRecursively(document.getPath(), indexingResponse);
        }
        indexingResponse.setIndexingTime(System.currentTimeMillis() - iTime);
        return indexingResponse;
    }

    private void indexFilesRecursively(String path, IndexingResponse indexingResponse) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;
        for (File f : list) {
            if (f.isDirectory()) {
                indexFilesRecursively(f.getAbsolutePath(), indexingResponse);
            } else {
                Map<String, List<Document>> read = readerCommand.getReaderByExtension(getFileExtension(f)).read(f, new ArrayList<>());
                balancer.createIndex(read);
                balancer.createIndex(createPathIndex(f));
                setIndexed(indexingResponse, 1, read.size() + 1);
            }
        }
    }

    private void setIndexed(IndexingResponse indexingResponse, int docsIndexed, int entitiesIndexed) {
        indexingResponse.setEntitiesIndexed(indexingResponse.getEntitiesIndexed() + entitiesIndexed);
        indexingResponse.setDocumentsIndexed(indexingResponse.getDocumentsIndexed() + docsIndexed);
    }

    private Map<String, List<Document>> createPathIndex(File file) {
        Map<String, List<Document>> map = new HashMap<>();
        map.put(file.getName(), Collections.singletonList(new Document(file.getAbsolutePath(), 0, DocumentType.PATH, null)));
        return map;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".") + 1;
        return name.substring(lastIndexOf);
    }
}
