package com.simplesearch.indexation;

import com.simplesearch.entity.document.Document;
import com.simplesearch.entity.session.SessionDTO;

import java.util.List;
import java.util.Map;

public interface DataIndexer {

    void makeIndexesFor(Map<String, List<Document>> indexes, SessionDTO session);

    int getIndexedCountBySessionId(String sessionId);
}
