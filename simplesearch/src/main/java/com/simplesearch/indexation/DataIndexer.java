package com.simplesearch.indexation;

import java.util.List;
import java.util.Map;

import com.simplesearch.model.internal.document.Document;
import com.simplesearch.model.session.SessionDTO;

public interface DataIndexer {

    void makeIndexesFor(Map<String, List<Document>> indexes, SessionDTO session);

    int getIndexedCountBySessionId(String sessionId);
}
