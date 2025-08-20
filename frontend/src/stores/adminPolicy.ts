// src/stores/adminPolicy.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import type { Policy } from '@/types/policy'

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
  const policies = ref<Policy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const fetchAdminPolicies = async (size: number = 1000) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/policies?size=${size}`, {
        method: 'GET',
        headers: {
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
      })

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

  const createPolicy = async (policyData: CreatePolicyRequest) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/admin/policies', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
        body: JSON.stringify(policyData),
      })

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

  const updatePolicy = async (id: number, policyData: Partial<CreatePolicyRequest>) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/admin/policies/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
        body: JSON.stringify(policyData),
      })

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

  const deletePolicy = async (id: number) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/admin/policies/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Failed to delete policy: ${response.status} ${errorText}`)
      }

      policies.value = policies.value.filter((p) => p.id !== id)
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to delete policy'
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
    deletePolicy,
  }
})
