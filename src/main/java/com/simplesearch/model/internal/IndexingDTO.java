package com.simplesearch.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import com.simplesearch.model.internal.document.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexingDTO {
    private int documentsIndexed;
    private int entitiesIndexed;
    private Map<String, List<Document>> total;
}
