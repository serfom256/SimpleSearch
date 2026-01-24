import { useState } from 'react'
import { Stack, TextInput, NumberInput, Checkbox, Button, Paper, Text } from '@mantine/core'
import { useSearch } from '../hooks/useSearch'

export function SearchForm() {
    const [toSearch, setToSearch] = useState('')
    const [count, setCount] = useState(100)
    const [distance, setDistance] = useState(1)
    const [sort, setSort] = useState(true)
    const [fuzziness, setFuzziness] = useState(true)

    const search = useSearch()

    const handleSearch = () => {
        search.mutate({
            toSearch,
            count,
            distance,
            sort,
            fuzziness,
        })
    }

    return (
        <Paper p="md" withBorder>
            <Stack>
                <TextInput
                    label="Search text"
                    placeholder="Enter text to search"
                    value={toSearch}
                    onChange={(e) => setToSearch(e.currentTarget.value)}
                />
                <NumberInput
                    label="Count"
                    value={count}
                    onChange={(val) => setCount(val as number ?? 100)}
                />
                <NumberInput
                    label="Distance"
                    value={distance}
                    onChange={(val) => setDistance(val as number ?? 1)}
                />
                <Checkbox
                    label="Sort by similarity"
                    checked={sort}
                    onChange={(e) => setSort(e.currentTarget.checked)}
                />
                <Checkbox
                    label="Fuzziness"
                    checked={fuzziness}
                    onChange={(e) => setFuzziness(e.currentTarget.checked)}
                />

                <Button onClick={handleSearch} loading={search.isPending}>
                    Search
                </Button>

                {search.isError && <Text c="red">{search.error.message}</Text>}

                {search.data && (
                    <Paper p="sm" withBorder>
                        <Text size="sm">
                            Found {search.data.header.founded} results in {search.data.header.Qtime}ms
                        </Text>
                        {search.data.resultList.map((item) => (
                            <Paper key={item.key} p="sm" mt="sm" withBorder>
                                <Text fw={500}>{item.key}</Text>
                                {item.metadata.map((meta, idx) => (
                                    <Text key={idx} size="sm">
                                        {meta.path} — position {meta.position} — type {meta.type}
                                    </Text>
                                ))}
                            </Paper>
                        ))}
                    </Paper>
                )}
            </Stack>
        </Paper>
    )
}