// frontend/src/stores/app.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface ApiEndpoints {
  // Authentication endpoints
  login: string
  register: string

  // User endpoints
  userPolicies: string
  userClaims: string

  // Admin endpoints
  adminPolicies: string
  adminClaims: string
  adminSupport: string
  adminPolicyUsers: (policyId: number) => string

  // Policy endpoints
  policies: string
  policyPurchase: string

  // Claims endpoints
  submitClaim: string
  updateClaimStatus: (claimId: number) => string

  // Support endpoints
  supportTickets: string
  createSupportTicket: string
  updateSupportTicket: (ticketId: number) => string
  fetchAllSupportTickets: string
  userSupportTickets: string
}

export interface AppConfig {
  appName: string
  version: string
  apiBaseUrl: string
  sessionCookie: string
  pagination: {
    defaultPageSize: number
    maxPageSize: number
  }
  ui: {
    toastDuration: number
    debounceDelay: number
  }
  features: {
    enableNotifications: boolean
    enableAnalytics: boolean
    enableDebugMode: boolean
  }
}

export const useAppStore = defineStore('app', () => {
  // Environment-based configuration
  const isDevelopment = computed(() => import.meta.env.DEV)
  const isProduction = computed(() => import.meta.env.PROD)

  // Base configuration
  const config = ref<AppConfig>({
    appName: 'Digital Insurance Management System',
    version: '2.0.0',
    apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    sessionCookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
    pagination: {
      defaultPageSize: 10,
      maxPageSize: 1000,
    },
    ui: {
      toastDuration: 5000,
      debounceDelay: 300,
    },
    features: {
      enableNotifications: true,
      enableAnalytics: !isDevelopment.value,
      enableDebugMode: isDevelopment.value,
    },
  })

  // API endpoints configuration
  const apiEndpoints = computed<ApiEndpoints>(() => ({
    // Authentication
    login: `${config.value.apiBaseUrl}/auth/login`,
    register: `${config.value.apiBaseUrl}/auth/register`,

    // User endpoints
    userPolicies: `${config.value.apiBaseUrl}/user/policies`,
    userClaims: `${config.value.apiBaseUrl}/user/claims`,

    // Admin endpoints
    adminPolicies: `${config.value.apiBaseUrl}/admin/policies`,
    adminClaims: `${config.value.apiBaseUrl}/admin/claims`,
    adminSupport: `${config.value.apiBaseUrl}/admin/support`,
    adminPolicyUsers: (policyId: number) =>
      `${config.value.apiBaseUrl}/admin/policies/${policyId}/users`,

    // Policy endpoints
    policies: `${config.value.apiBaseUrl}/policies`,
    policyPurchase: `${config.value.apiBaseUrl}/user/policy/purchase`,

    // Claims endpoints
    submitClaim: `${config.value.apiBaseUrl}/claim`,
    updateClaimStatus: (claimId: number) =>
      `${config.value.apiBaseUrl}/admin/claim/${claimId}/status`,

    // Support endpoints
    supportTickets: `${config.value.apiBaseUrl}/support`,
    createSupportTicket: `${config.value.apiBaseUrl}/support`,
    updateSupportTicket: (ticketId: number) =>
      `${config.value.apiBaseUrl}/admin/support/${ticketId}`,
    fetchAllSupportTickets: `${config.value.apiBaseUrl}/admin/support/fetchAll`,
    userSupportTickets: `${config.value.apiBaseUrl}/support/user/getTicketsByUser`,
  }))

  // Common request headers without auth
  const getBaseHeaders = () => {
    return {
      'Content-Type': 'application/json',
      Cookie: config.value.sessionCookie,
    }
  }

  // Get auth token from localStorage directly to avoid circular dependency
  const getAuthToken = () => {
    return localStorage.getItem('token')
  }

  // HTTP client wrapper
  const httpClient = {
    async get(url: string, options: RequestInit = {}) {
      const token = getAuthToken()
      return fetch(url, {
        method: 'GET',
        headers: {
          ...getBaseHeaders(),
          ...(token && { Authorization: `Bearer ${token}` }),
          ...options.headers,
        },
        credentials: 'include',
        ...options,
      })
    },

    async post(url: string, data?: any, options: RequestInit = {}) {
      const token = getAuthToken()
      return fetch(url, {
        method: 'POST',
        headers: {
          ...getBaseHeaders(),
          ...(token && { Authorization: `Bearer ${token}` }),
          ...options.headers,
        },
        credentials: 'include',
        body: data ? JSON.stringify(data) : undefined,
        ...options,
      })
    },

    async put(url: string, data?: any, options: RequestInit = {}) {
      const token = getAuthToken()
      return fetch(url, {
        method: 'PUT',
        headers: {
          ...getBaseHeaders(),
          ...(token && { Authorization: `Bearer ${token}` }),
          ...options.headers,
        },
        credentials: 'include',
        body: data ? JSON.stringify(data) : undefined,
        ...options,
      })
    },

    async patch(url: string, data?: any, options: RequestInit = {}) {
      const token = getAuthToken()
      return fetch(url, {
        method: 'PATCH',
        headers: {
          ...getBaseHeaders(),
          ...(token && { Authorization: `Bearer ${token}` }),
          ...options.headers,
        },
        credentials: 'include',
        body: data ? JSON.stringify(data) : undefined,
        ...options,
      })
    },

    async delete(url: string, options: RequestInit = {}) {
      const token = getAuthToken()
      return fetch(url, {
        method: 'DELETE',
        headers: {
          ...getBaseHeaders(),
          ...(token && { Authorization: `Bearer ${token}` }),
          ...options.headers,
        },
        credentials: 'include',
        ...options,
      })
    },
  }

  // Application state
  const isInitialized = ref(false)
  const theme = ref<'light' | 'dark'>('light')
  const sidebarCollapsed = ref(false)

  // Methods
  const initialize = async () => {
    if (isInitialized.value) return

    try {
      // Load user preferences from localStorage
      const savedTheme = localStorage.getItem('theme') as 'light' | 'dark'
      if (savedTheme) {
        theme.value = savedTheme
      }

      const savedSidebarState = localStorage.getItem('sidebarCollapsed')
      if (savedSidebarState !== null) {
        sidebarCollapsed.value = JSON.parse(savedSidebarState)
      }

      isInitialized.value = true
    } catch (error) {
      console.error('Failed to initialize app store:', error)
    }
  }

  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    localStorage.setItem('theme', theme.value)
  }

  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
    localStorage.setItem('sidebarCollapsed', JSON.stringify(sidebarCollapsed.value))
  }

  const updateConfig = (newConfig: Partial<AppConfig>) => {
    config.value = { ...config.value, ...newConfig }
  }

  return {
    // State
    config,
    apiEndpoints,
    isInitialized,
    theme,
    sidebarCollapsed,
    isDevelopment,
    isProduction,

    // HTTP client
    httpClient,

    // Methods
    initialize,
    toggleTheme,
    toggleSidebar,
    updateConfig,
    getBaseHeaders,
    getAuthToken,
  }
})