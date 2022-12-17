package com.opensearch.entity.shard;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardInfoHeader {
    private String cpu;
    private int threads;
    private String memory;
    private List<ShardState> shardsInfo;
}
