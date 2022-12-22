package com.simplesearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplesearch.entity.document.Document;
import lombok.Data;

import java.util.List;

@Data
public class LookupResult {
    private String key;
    @JsonIgnore
    private List<Integer> serializedIds; // todo create lookupResult dto
    private List<Document> metadata;
}
