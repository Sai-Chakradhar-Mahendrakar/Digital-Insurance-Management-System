// src/stores/claims.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'

export interface ClaimCreate {
  policyId: number
  claimDate: string
  claimAmount: number
  reason: string
}

export interface Claim {
  id: number
  userPolicyId: number
  claimDate: string
  claimAmount: number
  reason: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  reviewerComment: string | null
  resolvedDate: string | null
  policyName: string
  policyType: string
}

interface ClaimsResponse {
  claims: Claim[]
  totalElements: number
  totalPages: number
  size: number
  page: number
}

export const useClaimsStore = defineStore('claims', () => {
  const authStore = useAuthStore()

  const claims = ref<Claim[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const submitClaim = async (claimData: ClaimCreate) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/claim', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
        body: JSON.stringify(claimData),
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Failed to submit claim: ${response.status} ${errorText}`)
      }

      const newClaim: Claim = await response.json()
      claims.value.unshift(newClaim)

      return newClaim
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to submit claim'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchUserClaims = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/user/claims', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
      })

      if (!response.ok) {
        throw new Error(`Failed to fetch claims: ${response.status}`)
      }

      const data: ClaimsResponse = await response.json()
      claims.value = data.claims || []
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load claims'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const getClaimById = async (claimId: number) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/claim/${claimId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
      })

      if (!response.ok) {
        throw new Error(`Failed to fetch claim: ${response.status}`)
      }

      return await response.json()
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load claim'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    claims,
    isLoading,
    error,
    submitClaim,
    fetchUserClaims,
    getClaimById,
  }
})
