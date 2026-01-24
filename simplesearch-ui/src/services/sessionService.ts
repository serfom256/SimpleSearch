import { API } from '../api'
import { apiClient } from './axios'

export interface SessionStatusResponse {
    id: string
    status: 'IN_PROGRESS' | 'FAILED' | 'DONE'
    duration: string // ISO date
    indexed: number
    total: number
}

export function getSessionStatus(sessionId: string): Promise<SessionStatusResponse> {
    return apiClient.get<SessionStatusResponse>(API.SESSION(sessionId))
        .then(res => res.data)
}
