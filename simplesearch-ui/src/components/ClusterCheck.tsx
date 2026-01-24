import { Paper, Stack, Text, Table, Title, Loader } from '@mantine/core'
import { useClusterStats } from '../hooks/useClusterStats'
import type { ShardInfo } from '../services/shardService'

export function ClusterCheckForm() {
    const { data, isLoading, isError } = useClusterStats()

    if (isLoading) return <Loader />
    if (isError || !data) return <Text c="red">Failed to load cluster status</Text>

    return (
        <Paper p="md" withBorder>
            <Stack>
                <Title order={4}>Cluster Overview</Title>
                <Text>Indexed Total: {data.indexedTotal?.toLocaleString() ?? '---'}</Text>
                <Text>CPU: {data.cpu}</Text>
                <Text>Threads: {data.threads}</Text>
                <Text>Memory: {data.memory}</Text>

                <Title order={5}>Shards</Title>
                <Table striped highlightOnHover>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Indexed Documents</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.shardsInfo?.map((shard: ShardInfo) => (
                            <tr key={shard.name}>
                                <td>{shard.name}</td>
                                <td>{shard.indexed?.toLocaleString() ?? '---'}</td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </Stack>
        </Paper>
    )
}
