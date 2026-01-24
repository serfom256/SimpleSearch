import { useQuery } from '@tanstack/react-query'
import { getSessionStatus, type SessionStatusResponse } from '../services/sessionService'

export function useSessionStatus(sessionId: string) {
    return useQuery<SessionStatusResponse>({
        queryKey: ['session', sessionId],
        queryFn: () => getSessionStatus(sessionId),
        enabled: !!sessionId,
        refetchInterval: (query) => {
            const status = query.state.data?.status

            if (status === 'DONE' || status === 'FAILED') {
                return false
            }
            return 2000
        },
    })
}
