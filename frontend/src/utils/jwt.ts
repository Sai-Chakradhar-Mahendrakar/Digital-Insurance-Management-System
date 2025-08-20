// src/utils/jwt.ts
export interface JWTPayload {
  role: string
  sub: string // email/username
  iat: number // issued at
  exp: number // expiration
}

export const decodeJWT = (token: string): JWTPayload | null => {
  try {
    // JWT has 3 parts separated by dots
    const parts = token.split('.')
    if (parts.length !== 3) {
      throw new Error('Invalid JWT format')
    }

    // Decode the payload (second part)
    const payload = parts[1]
    // Add padding if necessary
    const paddedPayload = payload + '='.repeat((4 - (payload.length % 4)) % 4)
    const decoded = atob(paddedPayload)

    return JSON.parse(decoded) as JWTPayload
  } catch (error) {
    console.error('Failed to decode JWT:', error)
    return null
  }
}

export const isTokenExpired = (token: string): boolean => {
  const payload = decodeJWT(token)
  if (!payload) return true

  const currentTime = Math.floor(Date.now() / 1000)
  return payload.exp < currentTime
}

export const isAdminToken = (token: string): boolean => {
  const payload = decodeJWT(token)
  return payload?.role === 'ADMIN'
}
