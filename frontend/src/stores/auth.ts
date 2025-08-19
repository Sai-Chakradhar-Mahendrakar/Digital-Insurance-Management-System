// src/stores/auth.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue' // Add computed to the import
import type { User, LoginRequest, RegisterRequest, AuthResponse } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))

  const login = async (credentials: LoginRequest) => {
    try {
      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Cookie': 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6'
        },
        body: JSON.stringify(credentials)
      })

      if (!response.ok) {
        throw new Error('Login failed')
      }

      const data: AuthResponse = await response.json()
      
      token.value = data.token
      localStorage.setItem('token', data.token)
      
      // Set user data if available in response
      if (data.id) {
        user.value = {
          id: data.id,
          name: data.name || '',
          email: data.email || credentials.email,
          phone: data.phone || '',
          address: data.address || '',
          role: data.role || 'USER',
          token: data.token
        }
      }
    } catch (error) {
      throw new Error('Invalid credentials')
    }
  }

  const register = async (userData: RegisterRequest) => {
    try {
      const response = await fetch('http://localhost:8080/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Cookie': 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6'
        },
        body: JSON.stringify(userData)
      })

      if (!response.ok) {
        throw new Error('Registration failed')
      }

      const data: User = await response.json()
      
      user.value = data
      token.value = data.token || ''
      localStorage.setItem('token', data.token || '')
    } catch (error) {
      throw new Error('Registration failed')
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
  }

  const isAuthenticated = computed(() => !!token.value)

  return {
    user,
    token,
    login,
    register,
    logout,
    isAuthenticated
  }
})
