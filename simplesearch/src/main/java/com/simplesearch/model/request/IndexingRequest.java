package com.simplesearch.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexingRequest {
    private String path;
    private Set<String> mask = Set.of("txt", "pdf", "docx", "log", "md", "rtf", "csv", "xlsx", "xls");
    private Set<Character> separators = Collections.emptySet();

    public IndexingRequest(String path) {
        this.path = path;
    }
}
