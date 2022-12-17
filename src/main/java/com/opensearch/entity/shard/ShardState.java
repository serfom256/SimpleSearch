package com.opensearch.entity.shard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShardState {
    private String name;
    private int indexed;
}
