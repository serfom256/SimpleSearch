import { useMutation } from '@tanstack/react-query'
import { createIndex } from '../services/indexService'

export function useCreateIndex() {
    return useMutation({
        mutationFn: createIndex,
    })
}
