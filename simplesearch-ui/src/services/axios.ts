import axios from 'axios'

export const apiClient = axios.create({
    // baseURL: import.meta.env.VITE_API_BASE_URL,
    baseURL: 'http://localhost:8050',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000,
})
