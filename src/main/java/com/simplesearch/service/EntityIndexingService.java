package com.simplesearch.service;

import com.simplesearch.entity.IndexingDTO;
import com.simplesearch.entity.IndexingRequest;
import com.simplesearch.entity.IndexingResponse;
import com.simplesearch.entity.session.IndexingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityIndexingService {

    private final DataReadingService dataReadingService;
    private final SessionService sessionService;

    @Autowired
    public EntityIndexingService(DataReadingService dataReadingService, SessionService sessionService) {
        this.dataReadingService = dataReadingService;
        this.sessionService = sessionService;
    }

    public IndexingResponse save(IndexingRequest request) {
        long startTime = System.currentTimeMillis();
        IndexingDTO indexingDTO = dataReadingService.collectEntities(request);
        long indexingTime = System.currentTimeMillis() - startTime;
        IndexingSession session = sessionService.startIndexing(indexingDTO.getTotal(), indexingDTO.getEntitiesIndexed());
        return new IndexingResponse(session.getId(), indexingTime, indexingDTO.getDocumentsIndexed(), indexingDTO.getEntitiesIndexed());
    }

}
