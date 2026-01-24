import { API } from '../api'
import { apiClient } from './axios'


export interface CreateIndexRequest {
    path: string
    // mask?: string[]
    // separators?: string[]
}

export interface CreateIndexResponse {
    session: string
    indexingTime: number
    documentsIndexed: number
    entitiesIndexed: number
}


export function createIndex(
    payload: CreateIndexRequest
): Promise<CreateIndexResponse> {
    return apiClient
        .post<CreateIndexResponse>(API.INDEX, payload)
        .then((res) => res.data)
}
