package com.simplesearch.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document implements Serializable {
    private String path;
    private Integer position;
    private DocumentType type;
    private String content;
}
