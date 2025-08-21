// src/stores/adminClaims.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { useAppStore } from './app'

export interface AdminClaim {
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
  claims: AdminClaim[]
  totalElements: number
  totalPages: number
  size: number
  page: number
}

interface UpdateClaimStatus {
  status: 'APPROVED' | 'REJECTED'
  reviewerComment: string
}

export const useAdminClaimsStore = defineStore('adminClaims', () => {
  const authStore = useAuthStore()
  const appStore = useAppStore()

  const claims = ref<AdminClaim[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const fetchAllClaims = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await appStore.httpClient.get(appStore.apiEndpoints.adminClaims)

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

  const updateClaimStatus = async (claimId: number, updateData: UpdateClaimStatus) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await appStore.httpClient.put(
        appStore.apiEndpoints.updateClaimStatus(claimId),
        updateData
      )

      if (!response.ok) {
        throw new Error(`Failed to update claim: ${response.status}`)
      }

      const updatedClaim: AdminClaim = await response.json()

      // Update claim in the list
      const index = claims.value.findIndex((claim) => claim.id === updatedClaim.id)
      if (index !== -1) {
        claims.value[index] = updatedClaim
      }

      return updatedClaim
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to update claim'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    claims,
    isLoading,
    error,
    fetchAllClaims,
    updateClaimStatus,
  }
})
