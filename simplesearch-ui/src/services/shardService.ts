import { API } from "../api"
import { apiClient } from "./axios"

export interface ShardInfo {
    name: string
    indexed: number
}

export interface ClusterStatus {
    indexedTotal: number
    cpu: string
    threads: number
    memory: string
    shardsInfo: ShardInfo[]
}

export function getClusterStatus(): Promise<ClusterStatus> {
    return apiClient.get<ClusterStatus>(API.SHARDS).then(res => res.data)
}
