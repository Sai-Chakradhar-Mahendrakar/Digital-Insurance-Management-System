// src/stores/userPolicy.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'

export interface PolicyPurchase {
  policyId: number
  premiumPaid: number
}

export interface UserPolicy {
  id: number
  policyId: number
  userId: number
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ACTIVE' | 'EXPIRED'
  premiumPaid: number
  purchaseDate: string
  approvalDate?: string
  policy: {
    id: number
    name: string
    type: string
    description: string
    premiumAmount: number
    coverageAmount: number
    durationMonths: number
  }
}

export const useUserPolicyStore = defineStore('userPolicy', () => {
  const authStore = useAuthStore()
  const userPolicies = ref<UserPolicy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const purchasePolicy = async (purchaseData: PolicyPurchase) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/user/policy/purchase', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authStore.token}`,
          'Cookie': 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6'
        },
        credentials: 'include',
        body: JSON.stringify(purchaseData)
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Failed to purchase policy: ${response.status} ${errorText}`)
      }

      const result = await response.json()
      
      // Refresh user policies after purchase
      await fetchUserPolicies()
      
      return result
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to purchase policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchUserPolicies = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/user/policies', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authStore.token}`,
          'Cookie': 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6'
        },
        credentials: 'include'
      })

      if (!response.ok) {
        throw new Error(`Failed to fetch user policies: ${response.status}`)
      }

      const data = await response.json()
      userPolicies.value = Array.isArray(data) ? data : data.policies || []
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load user policies'
    } finally {
      isLoading.value = false
    }
  }

  return {
    userPolicies,
    isLoading,
    error,
    purchasePolicy,
    fetchUserPolicies
  }
})
