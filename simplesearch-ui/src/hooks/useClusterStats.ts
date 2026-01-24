import { useQuery } from '@tanstack/react-query'
import { getClusterStatus, type ClusterStatus } from '../services/shardService'

export function useClusterStats() {
    return useQuery<ClusterStatus>({
        queryKey: ['clusterStatus'],
        queryFn: getClusterStatus,
        refetchInterval: 5000, 
    })
}
