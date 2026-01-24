import { useState } from 'react'
import { Stack, TextInput, Button, Text, Paper, Progress } from '@mantine/core'
import { Dropzone, type FileWithPath } from '@mantine/dropzone'
import { useCreateIndex } from '../hooks/useCreateIndex'
import { useSessionStatus } from '../hooks/useSessionStatus'

export function CreateIndexForm() {
    const [path, setPath] = useState('')
    const [sessionId, setSessionId] = useState<string | null>(null)

    const createIndex = useCreateIndex()
    const sessionQuery = useSessionStatus(sessionId ?? '')

    const handleSubmit = () => {
        if (!path) return
        createIndex.mutate(
            { path },
            {
                onSuccess: (data) => {
                    setSessionId(data.session) // start polling
                },
            }
        )
    }

    const progress =
        sessionQuery.data && sessionQuery.data.total > 0
            ? (sessionQuery.data.indexed / sessionQuery.data.total) * 100
            : 0

    return (
        <Paper p="md" withBorder>
            <Stack gap="md">
                <Dropzone
                    onDrop={(files: FileWithPath[]) => {
                        if (files.length > 0) setPath(files[0].path || files[0].name)
                    }}
                    multiple={false}
                    maxSize={50 * 1024 * 1024}
                >
                    <Text ta="center" c="dimmed">
                        It's a perfect time for indexing now.(Drag a file or folder here or click to select)
                    </Text>
                </Dropzone>

                <TextInput
                    label="File or directory path"
                    placeholder="/path/to/local/file.extension"
                    value={path}
                    onChange={(e) => setPath(e.currentTarget.value)}
                />

                <Button onClick={handleSubmit} loading={createIndex.isPending}>
                    Index it!
                </Button>

                {sessionQuery.data && (
                    <div style={{ position: 'relative', width: '100%' }}>
                        <Progress
                            value={progress}
                            size="xl"
                            color={sessionQuery.data.status === 'DONE' ? 'green' : 'blue'}
                        />
                        <div
                            style={{
                                top: -4,
                                position: 'absolute',
                                width: '100%',
                                textAlign: 'center',
                                lineHeight: 'px',
                                fontWeight: 500,
                                color: 'black',
                            }}
                        >
                            {`${Math.round(progress)}%`}
                        </div>
                    </div>
                )}
                {createIndex.isError && (
                    <Text c="red">{(createIndex.error as Error).message}</Text>
                )}

                {createIndex.isSuccess && !sessionQuery.data && (
                    <Text>Indexing started, waiting for progress...</Text>
                )}

                {sessionQuery.data && sessionQuery.data.status === 'DONE' && (
                    <Text>Indexing complete! {sessionQuery.data.indexed} out of {sessionQuery.data.total} indexes created)</Text>
                )}
            </Stack>
        </Paper>
    )
}
