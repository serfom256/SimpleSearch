package com.simplesearch.service.files;

import com.simplesearch.model.response.IndexingResponse;

public interface FileIndexingService<T> {

    IndexingResponse indexFile(final T fileType);

}