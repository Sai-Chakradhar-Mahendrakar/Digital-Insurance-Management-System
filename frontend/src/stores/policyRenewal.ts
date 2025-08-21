// src/stores/policyRenewal.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'

export interface RenewablePolicy {
  id: number
  policyId: number
  policyName: string
  policyType: string
  startDate: string
  endDate: string
  status: string
  premiumPaid: number
  totalAmountClaimed: number
  coverageAmount: number
  userId: number
  userName: string
  userEmail: string
  userPhone: string
  userAddress: string
}

interface RenewablePoliciesResponse {
  userPolicies: RenewablePolicy[]
  totalElements: number
  totalPages: number
  size: number
  page: number
}

export const usePolicyRenewalStore = defineStore('policyRenewal', () => {
  const authStore = useAuthStore()

  const renewablePolicies = ref<RenewablePolicy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const fetchRenewablePolicies = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/user/policy/renewable', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
      })

      if (!response.ok) {
        throw new Error(`Failed to fetch renewable policies: ${response.status}`)
      }

      const data: RenewablePoliciesResponse = await response.json()
      renewablePolicies.value = data.userPolicies || []
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load renewable policies'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const renewPolicy = async (policyId: number) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/user/policy/${policyId}/renew`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
        body: '',
      })

      if (!response.ok) {
        throw new Error(`Failed to renew policy: ${response.status}`)
      }

      const renewedPolicy: RenewablePolicy = await response.json()

      // Update policy in the list
      const index = renewablePolicies.value.findIndex(
        (policy) => policy.policyId === renewedPolicy.policyId,
      )
      if (index !== -1) {
        renewablePolicies.value[index] = renewedPolicy
      }

      return renewedPolicy
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to renew policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    renewablePolicies,
    isLoading,
    error,
    fetchRenewablePolicies,
    renewPolicy,
  }
})
