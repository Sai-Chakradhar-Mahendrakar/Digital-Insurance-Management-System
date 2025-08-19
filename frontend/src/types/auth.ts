// src/types/auth.ts
export interface User {
  id: string
  name: string
  email: string
  phone: string
  address: string
  role: string
  token?: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  name: string
  email: string
  password: string
  phone: string
  address: string
  role: string
}

export interface AuthResponse {
  id?: string
  name?: string
  email?: string
  phone?: string
  address?: string
  role?: string
  token: string
}
