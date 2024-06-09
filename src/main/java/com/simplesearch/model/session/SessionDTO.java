package com.simplesearch.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDTO {
    private Date sessionStart;
    private IndexingSession indexingSession;
}
