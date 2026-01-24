const API_PREFIX = '/simplesearch/api/v1'

export const API = {
    INDEX: `${API_PREFIX}/index`,
    SESSION: (session: string) => `${API_PREFIX}/session/${session}`,
    SHARDS: `${API_PREFIX}/shards`,
    SEARCH: `${API_PREFIX}/search`,
} as const
