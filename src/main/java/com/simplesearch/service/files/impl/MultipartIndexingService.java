package com.simplesearch.service.files.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.simplesearch.model.internal.IndexingDTO;
import com.simplesearch.model.request.IndexingRequest;
import com.simplesearch.model.response.IndexingResponse;
import com.simplesearch.model.session.IndexingSession;
import com.simplesearch.service.SessionService;
import com.simplesearch.service.files.DataReadingService;
import com.simplesearch.service.files.FileIndexingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MultipartIndexingService implements FileIndexingService<MultipartFile> {

    private final DataReadingService dataReadingService;
    private final SessionService sessionService;

    @Override
    public IndexingResponse indexFile(final MultipartFile multipartFile) {
        long startTime = System.currentTimeMillis();

        final File file = createTemporaryFile(multipartFile);
        final IndexingRequest request = buildRequest(file);

        IndexingDTO indexingDTO = dataReadingService.collectEntities(request);
        long indexingTime = System.currentTimeMillis() - startTime;
        IndexingSession session = sessionService.startIndexing(indexingDTO.getTotal(),
                indexingDTO.getEntitiesIndexed());

        return new IndexingResponse(session.getId(), indexingTime, indexingDTO.getDocumentsIndexed(),
                indexingDTO.getEntitiesIndexed());
    }

    private IndexingRequest buildRequest(final File file) {
        return IndexingRequest.builder()
                .path(file.getAbsolutePath())
                .separators(Set.of())
                .mask(Set.of())
                .build();
    }

    private File createTemporaryFile(final MultipartFile multipartFile) {
        final String originalName = multipartFile.getOriginalFilename();
        try {
            File tempFile = File.createTempFile(
                    originalName.substring(0, originalName.lastIndexOf(".")),
                    originalName.substring(originalName.lastIndexOf(".")));

            try (InputStream input = multipartFile.getInputStream();
                    OutputStream output = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024 * 8];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Couldn't handle given file :(");
        }
    }
}
