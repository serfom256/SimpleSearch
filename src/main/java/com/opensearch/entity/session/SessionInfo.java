package com.opensearch.entity.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfo {
    private String sessionUUID;
    private SessionStatus status;
    private Date createTime;
    private Integer indexed;
    private Integer total;
}
