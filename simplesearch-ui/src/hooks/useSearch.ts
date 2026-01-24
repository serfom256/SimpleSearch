import { useMutation } from '@tanstack/react-query'
import { searchFor, type SearchRequest, type SearchResponse } from '../services/searchService'

export function useSearch() {
    return useMutation<SearchResponse, Error, SearchRequest>({
        mutationFn: (body) => searchFor(body),
    })
}