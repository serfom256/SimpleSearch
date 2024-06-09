package com.simplesearch.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexingSession {
    private String id;
    private SessionStatus status;
    private Date duration;
    private int indexed;
    private int total;
}
