package com.simplesearch.model.internal.shard;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardInfoHeader {
    private Integer indexedTotal;
    private String cpu;
    private int threads;
    private String memory;
    private List<ShardState> shardsInfo;
}
