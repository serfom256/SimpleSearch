import { API } from '../api'
import { apiClient } from './axios'


export interface SearchRequest {
    toSearch: string
    count?: number
    distance?: number
    sort?: boolean
    fuzziness?: boolean
}

export interface SearchMetadata {
    path: string
    position: number
    type: string
    content?: string | null
}

export interface SearchResultItem {
    key: string
    metadata: SearchMetadata[]
}

export interface SearchResponseHeader {
    normalizingTime: number | null
    sorted: boolean
    founded: number
    shardsUsed: number
    Qtime: number
}

export interface SearchResponse {
    header: SearchResponseHeader
    resultList: SearchResultItem[]
}

export function searchFor(body: SearchRequest): Promise<SearchResponse> {
    return apiClient.post<SearchResponse>(API.SEARCH, body).then(res => res.data)
}