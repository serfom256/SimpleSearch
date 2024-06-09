package com.simplesearch.service.files.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.simplesearch.model.internal.IndexingDTO;
import com.simplesearch.model.request.IndexingRequest;
import com.simplesearch.model.response.IndexingResponse;
import com.simplesearch.model.session.IndexingSession;
import com.simplesearch.service.SessionService;
import com.simplesearch.service.files.DataReadingService;
import com.simplesearch.service.files.FileIndexingService;

@Service
@RequiredArgsConstructor
public class LocalFileIndexingService implements FileIndexingService<IndexingRequest> {

    private final DataReadingService dataReadingService;
    private final SessionService sessionService;

    @Override
    public IndexingResponse indexFile(IndexingRequest request) {
        long startTime = System.currentTimeMillis();
        IndexingDTO indexingDTO = dataReadingService.collectEntities(request);
        long indexingTime = System.currentTimeMillis() - startTime;
        IndexingSession session = sessionService.startIndexing(indexingDTO.getTotal(),
                indexingDTO.getEntitiesIndexed());
        return new IndexingResponse(session.getId(), indexingTime, indexingDTO.getDocumentsIndexed(),
                indexingDTO.getEntitiesIndexed());
    }

}
