package com.opensearch.service;

import com.opensearch.entity.IndexResponse;
import com.opensearch.entity.ObjectMetadata;
import com.opensearch.exceptions.PathNotFoundException;
import com.opensearch.util.ReaderCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class FileReadingService {

    private final ReaderCommand readerCommand;

    public FileReadingService() {
        readerCommand = new ReaderCommand();
    }

    public IndexResponse read(String file) {
        File dataDir = new File(file);
        if (!dataDir.exists()) throw new PathNotFoundException(file);
        if (dataDir.isDirectory()) {
            List<Map<String, ObjectMetadata>> collect = Arrays.stream(dataDir.listFiles())
                    .map(f -> readerCommand.getReaderByExtension(getFileExtension(f))
                            .read(f, new ArrayList<>()))
                    .collect(Collectors.toList());

            log.info(collect);

        }
        return null;
    }

    private String getFileExtension(File file) {
        return "";
    }
}
