import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { useAppStore } from './app'
import type { Policy } from '@/types/policy'
import type { PolicyUser, PolicyUsersResponse } from '@/types/admin'

export interface CreatePolicyRequest {
  name: string
  type: string
  description: string
  premiumAmount: number
  coverageAmount: number
  durationMonths: number
  renewalPremiumRate: number
}

export const useAdminPolicyStore = defineStore('adminPolicy', () => {
  const authStore = useAuthStore()
  const appStore = useAppStore()
  const policies = ref<Policy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const createPolicy = async (policyData: CreatePolicyRequest) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await appStore.httpClient.post(
        appStore.apiEndpoints.adminPolicies,
        policyData
      )
      // console.log(policyData)
      // console.log(String(response.headers))

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Failed to create policy: ${response.status} ${errorText}`)
      }

      const newPolicy: Policy = await response.json()
      policies.value.push(newPolicy)

      // Refresh the list to get the latest data
      await fetchAdminPolicies()

      return newPolicy
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to create policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchAdminPolicies = async (size: number = 1000) => {
    isLoading.value = true
    error.value = null

    try {
      const url = `${appStore.apiEndpoints.policies}?size=${size}`
      const response = await appStore.httpClient.get(url)

      if (!response.ok) {
        throw new Error(`Failed to fetch policies: ${response.status} ${response.statusText}`)
      }

      const data = await response.json()

      // Handle different response formats
      if (Array.isArray(data)) {
        policies.value = data
      } else if (data.policies && Array.isArray(data.policies)) {
        policies.value = data.policies
      } else if (data.content && Array.isArray(data.content)) {
        policies.value = data.content // Spring Boot pagination format
      } else {
        policies.value = []
        console.warn('Unexpected response format:', data)
      }

      console.log(`Loaded ${policies.value.length} policies`)
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load admin policies'
      console.error('Admin policy fetch error:', err)
    } finally {
      isLoading.value = false
    }
  }

  const updatePolicy = async (id: number, policyData: Partial<CreatePolicyRequest>) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await appStore.httpClient.put(
        `${appStore.apiEndpoints.adminPolicies}/${id}`,
        policyData
      )

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Failed to update policy: ${response.status} ${errorText}`)
      }

      const updatedPolicy: Policy = await response.json()
      const index = policies.value.findIndex((p) => p.id === id)
      if (index !== -1) {
        policies.value[index] = updatedPolicy
      }

      return updatedPolicy
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to update policy'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchPolicyUsers = async (policyId: number) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await appStore.httpClient.get(
        appStore.apiEndpoints.adminPolicyUsers(policyId)
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch policy users: ${response.status} ${response.statusText}`)
      }

      const data: PolicyUsersResponse = await response.json()
      return data.content || []
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load policy users'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    policies,
    isLoading,
    error,
    createPolicy,
    fetchAdminPolicies,
    updatePolicy,
    fetchPolicyUsers,
  }
})
