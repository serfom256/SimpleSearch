package com.opensearch.util;

import com.opensearch.entity.ObjectMetadata;

import java.util.List;

public interface Reader {
    List<ObjectMetadata> read(String file);
}
