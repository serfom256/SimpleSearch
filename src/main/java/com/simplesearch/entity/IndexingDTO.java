package com.simplesearch.entity;

import com.simplesearch.entity.document.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexingDTO {
    private int documentsIndexed;
    private int entitiesIndexed;
    private Map<String, List<Document>> total;
}
