package com.simplesearch.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplesearch.model.internal.document.Document;

import lombok.Data;

import java.util.List;

@Data
public class LookupResult {
    private String key;
    @JsonIgnore
    private List<Integer> serializedIds; // todo create lookupResult dto
    private List<Document> metadata;
}
