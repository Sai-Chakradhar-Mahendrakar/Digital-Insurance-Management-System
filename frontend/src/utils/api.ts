// src/utils/api.ts
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true, // This ensures cookies are sent
  headers: {
    'Content-Type': 'application/json',
    'Cookie': 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6'
  }
})

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message)
    return Promise.reject(error)
  }
)

export default api

// Usage in store:
// const response = await api.get('/policies?size=1000')
// const policies = response.data
