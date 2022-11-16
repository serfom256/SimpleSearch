package com.opensearch.service;

import com.opensearch.core.LoadBalancer;
import com.opensearch.entity.IndexingRequest;
import com.opensearch.entity.IndexingResponse;
import com.opensearch.exceptions.PathNotFoundException;
import com.opensearch.util.ReaderCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

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
        long iTime = System.currentTimeMillis();
        if (!dataDir.exists()) throw new PathNotFoundException(document.getPath());
        File f = new File(document.getPath());
        int indexed = 0;
        if (f.isFile()) {
            balancer.createIndex(readerCommand.getReaderByExtension(getFileExtension(f)).read(f, new ArrayList<>()));
            indexed++;
        } else {
            indexed = indexFilesRecursively(document.getPath(), 0);
        }
        return new IndexingResponse(System.currentTimeMillis() - iTime, indexed, 0);
    }

    private int indexFilesRecursively(String path, int indexed) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return indexed;
        for (File f : list) {
            if (f.isDirectory()) {
                indexFilesRecursively(f.getAbsolutePath(), indexed);
            } else {
                indexed++;
                balancer.createIndex(readerCommand.getReaderByExtension(getFileExtension(f)).read(f, new ArrayList<>()));
            }
        }
        return indexed;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".") + 1;
        return name.substring(lastIndexOf);
    }
}
