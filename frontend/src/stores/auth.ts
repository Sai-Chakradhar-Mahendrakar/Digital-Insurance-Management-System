import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { decodeJWT, isTokenExpired, isAdminToken, type JWTPayload } from '@/utils/jwt'
import type { User, LoginRequest, RegisterRequest, AuthResponse } from '@/types/auth'
import { useAppStore } from '@/stores/app';


export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const tokenPayload = ref<JWTPayload | null>(null)
  const appStore = useAppStore();

  // Initialize token payload if token exists
  const initializeAuth = () => {
    if (token.value) {
      const payload = decodeJWT(token.value)
      if (payload && !isTokenExpired(token.value)) {
        tokenPayload.value = payload
        user.value = {
          id: '',
          name: payload.sub.split('@')[0],
          email: payload.sub,
          phone: '',
          address: '',
          role: payload.role,
          token: token.value,
        }
      } else {
        logout()
      }
    }
  }

  const login = async (credentials: LoginRequest) => {
    try {
      // Import app store dynamically to avoid circular dependency
      const { useAppStore } = await import('./app')
      const appStore = useAppStore()

      const response = await appStore.httpClient.post(
        appStore.apiEndpoints.login,
        credentials,
        {
          headers: {
            'Content-Type': 'application/json',
            Cookie: appStore.config.sessionCookie,
          },
        }
      )

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || 'Login failed')
      }

      const data: AuthResponse = await response.json()
      setAuthData(data, credentials.email)
    } catch (error: unknown) {
      // Proper error handling for unknown type
      if (error instanceof Error) {
        throw new Error(error.message)
      } else if (typeof error === 'string') {
        throw new Error(error)
      } else {
        throw new Error('Invalid credentials')
      }
    }
  }

  const adminLogin = async (credentials: LoginRequest) => {
    try {
      // Direct fetch for admin login to avoid circular dependency issues
      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        body: JSON.stringify(credentials),
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || 'Admin login failed')
      }

      const data: AuthResponse = await response.json()

      // Verify the token has ADMIN role
      if (!isAdminToken(data.token)) {
        throw new Error('Insufficient permissions - Admin role required')
      }

      setAuthData(data, credentials.email)
    } catch (error: unknown) {
      // Type-safe error handling
      if (error instanceof Error) {
        throw new Error(error.message)
      } else if (typeof error === 'string') {
        throw new Error(error)
      } else {
        throw new Error('Invalid admin credentials')
      }
    }
  }

  const setAuthData = (data: AuthResponse, email: string) => {
    token.value = data.token
    localStorage.setItem('token', data.token)

    // Decode JWT payload
    const payload = decodeJWT(data.token)
    tokenPayload.value = payload

    if (payload) {
      user.value = {
        id: data.id || '',
        name: data.name || payload.sub.split('@')[0],
        email: data.email || payload.sub,
        phone: data.phone || '',
        address: data.address || '',
        role: payload.role,
        token: data.token,
      }
    }
  }

  const register = async (userData: RegisterRequest) => {
    try {
      // Import app store dynamically to avoid circular dependency
      const { useAppStore } = await import('./app')
      const appStore = useAppStore()

      const response = await appStore.httpClient.post(
        appStore.apiEndpoints.register,
        userData
      )

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || 'Registration failed')
      }

      const data: User = await response.json()

      user.value = data
      token.value = data.token || ''
      localStorage.setItem('token', data.token || '')

      if (data.token) {
        tokenPayload.value = decodeJWT(data.token)
      }
    } catch (error: unknown) {
      // Handle unknown error type safely
      if (error instanceof Error) {
        throw new Error(error.message)
      } else if (typeof error === 'string') {
        throw new Error(error)
      } else {
        throw new Error('Registration failed')
      }
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    tokenPayload.value = null
    localStorage.removeItem('token')
  }

  const checkTokenValidity = () => {
    if (token.value && isTokenExpired(token.value)) {
      logout()
      return false
    }
    return true
  }

  const isAuthenticated = computed(() => {
    if (!token.value) return false
    return checkTokenValidity()
  })

  const isAdmin = computed(() => {
    if (!tokenPayload.value) return false
    return tokenPayload.value.role === 'ADMIN'
  })

  const tokenInfo = computed(() => ({
    payload: tokenPayload.value,
    isExpired: token.value ? isTokenExpired(token.value) : true,
    timeToExpiry: tokenPayload.value ? tokenPayload.value.exp - Math.floor(Date.now() / 1000) : 0,
  }))

  // Initialize on store creation
  initializeAuth()

  return {
    user,
    token,
    tokenPayload,
    tokenInfo,
    login,
    adminLogin,
    register,
    logout,
    checkTokenValidity,
    isAuthenticated,
    isAdmin,
  }
})